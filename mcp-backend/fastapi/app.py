# app.py
from fastapi import FastAPI
from pydantic import BaseModel
import asyncio

# app.py 상단
from client.client_resources_with_llm import query_llm, mcp_client, to_openai_schema


app = FastAPI()

# 초기화 변수
tool_schemas = []

@app.on_event("startup")
async def startup_event():
    global tool_schemas
    await mcp_client.__aenter__()  # async with 없이 수동 진입
    tools = await mcp_client.list_tools()
    tool_schemas = [to_openai_schema(tool) for tool in tools]

@app.on_event("shutdown")
async def shutdown_event():
    await mcp_client.__aexit__(None, None, None)  # 수동 종료

class LLMQuery(BaseModel):
    query: str

@app.post("/api/mcp/queryLLM")
async def query_llm_endpoint(payload: LLMQuery):
    answer = await query_llm(payload.query, tool_schemas)
    return {"result": answer}