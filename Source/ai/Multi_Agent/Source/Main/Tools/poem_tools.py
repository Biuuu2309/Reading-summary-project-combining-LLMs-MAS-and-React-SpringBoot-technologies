from langchain_community.chat_models import ollama, ChatOllama
from langchain.tools import Tool
from langchain.agents import create_react_agent, AgentExecutor
from langchain import hub
from typing import TypedDict, Annotated, List
import operator

llm = ChatOllama(model="llama3") # <-- Sử dụng model bạn đã kéo về, ví dụ "llama3", "mistral"
prompt = hub.pull("hwchase17/react")

class AgentState(TypedDict):
    # Thông điệp của người dùng
    input: str
    # Đầu ra của từng agent sẽ được nối vào đây
    messages: Annotated[List[str], operator.add]
    
# Tool cho Agent Nhà Thơ
def write_poem(theme: str) -> str:
    """Viết một bài thơ ngắn về chủ đề được cho."""
    poem = llm.invoke(f"Hãy viết một bài thơ ngắn 4 câu về chủ đề: {theme}")
    return poem.content

poem_tool = Tool(
    name="PoemWriter",
    func=write_poem,
    description="Useful for writing a short poem about a given theme. Input should be the theme."
)