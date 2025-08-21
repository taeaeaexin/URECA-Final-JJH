from dotenv import load_dotenv
load_dotenv()
import asyncio
from typing import Any, Dict, List
import asyncio, json
# --------------------- 클라이언트 초기화 ---------------------
from openai import AsyncOpenAI
from fastmcp import Client as MCPClient

llm_client = AsyncOpenAI()  # OpenAI LLM 클라이언트
mcp_client = MCPClient("../app.py")   # FastMCP 리소스 서버 클라이언트

# MCP -> OpenAI 툴 스키마 변환
def to_openai_schema(tool) -> Dict[str, Any]:
    # 입력 스키마 추출
    raw_schema = (
        getattr(tool, "inputSchema", None)
        or getattr(tool, "input_schema", None)
        or getattr(tool, "parameters", None)
    )

    # 다양한 형태를 dict(JSON-Schema) 로 통일
    if raw_schema is None:
        schema: Dict[str, Any] = {"type": "object", "properties": {}, "additionalProperties": True}

    elif isinstance(raw_schema, dict):
        schema = raw_schema

    elif hasattr(raw_schema, "model_json_schema"):          # Pydantic v2 모델
        schema = raw_schema.model_json_schema()

    elif isinstance(raw_schema, list):                      # list[dict]
        props, required = {}, []
        for p in raw_schema:
            props[p["name"]] = {
                "type": p["type"],
                "description": p.get("description", ""),
            }
            if p.get("required", True):
                required.append(p["name"])
        schema = {"type": "object", "properties": props}
        if required:
            schema["required"] = required

    else:                                                   # 알 수 없는 형식
        schema = {"type": "object", "properties": {}, "additionalProperties": True}

    # 필수 키 보강
    schema.setdefault("type", "object")
    schema.setdefault("properties", {})
    if "required" not in schema:
        schema["required"] = list(schema["properties"].keys())  # 모두 optional 로 두고 싶다면 []

    # OpenAI 툴 JSON 반환
    return {
        "type": "function",
        "name": tool.name,
        "description": getattr(tool, "description", ""),
        "parameters": schema,
    }

# mcp와 연결 안하는 query_llm
# async def query_llm(question: str) -> str:
#     resp = await llm_client.responses.create(
#         model="gpt-4o",
#         input=[{"role": "user", "content": question}]
#     )
#     return resp.output_text

async def query_llm(question: str, tool_schemas: List[Dict[str, Any]]) -> str:
    ########## 1차 요청 ##########
    resp = await llm_client.responses.create(
        model="gpt-4o",
        input=[{"role": "user", "content": question}],
        tools=tool_schemas,
    )

    ##### 툴 호출이 없을 때 #####
    tool_calls = [o for o in resp.output if getattr(o, "type", "") == "function_call"]
    if not tool_calls:
        print("툴 호출 없음, 바로 답변 반환")
        return resp.output_text

    ##### 결과를 담을 next_input 에 user 질문 유지 #####
    next_input: List[Any] = [{"role": "user", "content": question}]

    ########## 각 툴 호출 처리 ##########
    for call in tool_calls:
        # MCP 서버 실행 (arguments 는 str일 수도 dict일 수도 있음)
        print(f"call.name: {call.name}, call.id: {call.call_id}")
        args = call.arguments
        if isinstance(args, str):
            args = json.loads(args)
            print(f"args (str): {args}")
        result = await mcp_client.call_tool(call.name, args)

        # 호출 자체를 메시지 배열에 추가
        next_input.append(call)
        # 실행 결과를 function_call_output 형식으로 추가
        next_input.append(
            {
                "type": "function_call_output",
                "call_id": call.call_id,
                "output": str(result),
            }
        )

    ########## 2차 호출 -> 최종 답변 ##########
    final = await llm_client.responses.create(
        model="gpt-4o",
        input=next_input,
    )
    return final.output_text      

async def main():
    async with mcp_client:
        print(f"MCP connected → {mcp_client.is_connected()}") # MCP 서버 연결 상태 출력

        mcp_tools = await mcp_client.list_tools() # MCP 서버의 도구 목록 가져오기
        tool_schemas = [to_openai_schema(tool) for tool in mcp_tools]

        print("MCP 서버 도구 목록")
        for tool in mcp_tools:
            print(f"tool: {tool}")
    
        for tool in mcp_tools:
                print(f"OpenAI tool schema: {to_openai_schema(tool)}")

        while True:
            question = input("질문을 입력하세요 (exit 입력 시 종료): ")
            if question.strip().lower() == "exit":
                break
            
            # answer = await query_llm(question)
            answer = await query_llm(question, tool_schemas)

            print(f"\nLLM 답변 ▶ {answer}\n")

    print(f"\nMCP connected → {mcp_client.is_connected()}")

if __name__ == "__main__":
    asyncio.run(main())