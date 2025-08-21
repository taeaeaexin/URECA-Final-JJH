# my_server.py
from fastmcp import FastMCP
import pymysql
import os
from dotenv import load_dotenv
from datetime import datetime
import pytz
load_dotenv() 


# 서버의 이름을 지정하여 인스턴스화합니다.
mcp = FastMCP(name="내 첫 MCP 서버")

print("FastMCP 서버 객체가 생성되었습니다.")

@mcp.tool()
def greet(name: str) -> str:
    """간단한 인사 말합니다."""
    return f"안녕하세요, {name}님!"

@mcp.tool()
def add(a: int, b: int) -> int:
    """두 수를 더합니다."""
    return a + b

print("도구 'greet'와 'add'가 추가되었습니다.")

RDS_HOST = os.getenv("RDS_HOST")
RDS_PORT = int(os.getenv("RDS_PORT", "3306"))
RDS_USER = os.getenv("RDS_USER")
RDS_PASS = os.getenv("RDS_PASS")
RDS_DB = os.getenv("RDS_DB")

@mcp.tool()
def get_kst_time() -> str:
    """한국 표준시 현재 시간을 ISO 8601 문자열로 반환합니다."""
    kst = pytz.timezone('Asia/Seoul')
    now_kst = datetime.now(kst)
    return now_kst.strftime("%Y-%m-%dT%H:%M:%S")

@mcp.tool()
def execute_sql(sql: str) -> str:
    """Execute SQL queries on RDS MySQL database"""
    if not sql or not sql.strip():
        return "Error: Empty SQL query"
    
    sql = sql.strip()
    dangerous_keywords = ['DROP DATABASE', 'DROP SCHEMA', 'TRUNCATE']
    if any(keyword in sql.upper() for keyword in dangerous_keywords):
        return "Error: Dangerous SQL operation not allowed"
    
    conn = None
    try:
        conn = pymysql.connect(
            host=RDS_HOST,
            port=RDS_PORT,
            user=RDS_USER,
            password=RDS_PASS,
            database=RDS_DB,
            connect_timeout=10
        )
        with conn.cursor() as cur:
            cur.execute(sql)
            if sql.upper().startswith("SELECT"):
                rows = cur.fetchall()
                if cur.description:
                    cols = [d[0] for d in cur.description]
                    result = f"Columns: {cols}\nRows: {list(rows)}"
                    return result
                else:
                    return "No results"
            else:
                conn.commit()
                return f"Query executed successfully. Affected rows: {cur.rowcount}"
    except Exception as e:
        return f"Database error: {str(e)}"
    finally:
        if conn:
            conn.close()

print("도구 'execute_sql'이 추가되었습니다."+RDS_HOST)

@mcp.prompt("summarize")
async def summarize_prompt(text: str) -> list[dict]:
    """제공된 텍스트를 요약하는 프롬프트를 생성합니다."""
    return [
        {"role": "system", "content": "당신은 요약에 능숙한 유용한 조수입니다."},
        {"role": "user", "content": f"다음 텍스트를 요약해 주세요:\n\n{text}"}
    ]

print("프롬프트 'summarize'가 추가되었습니다.")
