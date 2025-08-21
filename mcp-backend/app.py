from my_server import mcp

if __name__ == "__main__":
    print("\n--- __main__을 통해 FastMCP 서버 시작 중 ---")
    # 이는 서버를 시작합니다. 일반적으로 기본적으로 stdio 전송을 사용합니다.
    mcp.run()
    # http 기반으로 서버를 동작시키기 위해 stdio에서 streamable http 방식으로 변경합니다.
    # mcp.run(
    #     transport="streamable-http",
    #     path="/",
    # )