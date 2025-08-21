from fastmcp import Client 
from my_server import mcp
import asyncio

async def test_server_locally():
    print("\n--- 로컬 서버 테스트 중 ---")
    # 클라이언트가 서버 객체를 가리키도록 설정합니다.
    client = Client(mcp)

    # 클라이언트는 비동작적이므로 비동기 컨텍스트 관리자를 사용합니다.
    async with client:

        greet_result = await client.call_tool("greet", {"name": "FastMCP 사용자"})
        print(f"greet 결과: {greet_result}")
        
        add_result = await client.call_tool("add", {"a": 5, "b": 7})
        print(f"add 결과: {add_result}")
        
        add_result = await client.call_tool("execute_sql", {"sql": "SELECT * FROM post"})
        print(f"add 결과: {add_result}")

        # 'summarize' 프롬프트 구조 얻기(여기서 LLM 호출을 실행하지 않음)
        prompt_messages = await client.get_prompt("summarize", {"text": "이것은 일부 텍스트입니다."})
        print(f"요약 프롬프트 구조: {prompt_messages}")

# 로컬 테스트 함수 실행
asyncio.run(test_server_locally())