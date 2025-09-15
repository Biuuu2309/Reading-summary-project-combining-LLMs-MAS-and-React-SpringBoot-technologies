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
    
# Tool cho Agent Phân Tích Cảm Xúc
def analyze_sentiment(text: str) -> str:
    """Phân tích cảm xúc của đoạn văn bản. Trả về Positive, Negative hoặc Neutral."""
    # Ở đây để đơn giản, chúng ta dùng LLM để phân tích. Trong thực tế có thể dùng model chuyên dụng.
    analysis = llm.invoke(f"Phân tích cảm xúc của đoạn văn sau, chỉ trả về 1 từ 'Positive', 'Negative' hoặc 'Neutral': {text}")
    return analysis.content

sentiment_tool = Tool(
    name="SentimentAnalyzer",
    func=analyze_sentiment,
    description="Useful for analyzing the sentiment of a given text. Input should be a string."
)