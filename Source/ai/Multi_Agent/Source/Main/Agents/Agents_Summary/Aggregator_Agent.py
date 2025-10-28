from dotenv import load_dotenv
load_dotenv()

# from langchain_openai import ChatOpenAI # <-- Dòng cũ, comment lại
from langchain_community.chat_models import ChatOllama # <-- Dòng mới
from langchain.agents import create_react_agent, AgentExecutor
from langchain import hub
from langchain.tools import Tool
from langgraph.graph import END, StateGraph
from typing import TypedDict, Annotated, List, Any, Dict, Literal
import operator
import requests
from langchain_core.messages import HumanMessage, AIMessage, SystemMessage
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from Source.ai.Multi_Agent.Source.Main.Memory.memory.memory import memory_manager
# Khởi tạo model LLM Local từ Ollama
llm = ChatOllama(model="llama3:8b") # <-- Sử dụng model bạn đã kéo về, ví dụ "llama3", "mistral"
class AgentState(TypedDict):
    messages: List[Any]
    current_agent: str
    needs_user_input: bool
    conversation_stage: Literal["greeting", "reader_ocr", "spellchecker", "extractor", "abstracter", "grade_calibrator", "evaluator", "aggregator", "completed"]

AGGREGATOR_SYSTEM = """Bạn là Aggregator Agent chuyên nghiệp. Hãy:
Tùy thuộc vào thể loại tóm tắt TRÍCH XUẤT hay DIỄN GIẢI, bạn sẽ tổng hợp các bản tóm tắt thành bản tóm tắt cuối cùng theo cách phù hợp sao cho ngắn gọn, mạch lạc và dễ hiểu.
"""

def aggregator_agent(state: AgentState):
    messages = state["messages"]
    memory = memory_manager.get_memory()
    
    if not messages:
        query = next((m.content for m in reversed(messages) if isinstance(m, HumanMessage)), "")
        context = memory_manager.get_context_summary(include_long_term=True, current_input=query)
        prompt = [SystemMessage(content=f"{AGGREGATOR_SYSTEM}\n\nContext từ memory:\n{context}")]
    else:
        query = next((m.content for m in reversed(messages) if isinstance(m, HumanMessage)), "")
        context = memory_manager.get_context_summary(include_long_term=True, current_input=query)
        prompt = [
            SystemMessage(content=f"{AGGREGATOR_SYSTEM}\n\nContext từ memory:\n{context}"),
            *messages,
        ]
    
    response = llm.invoke(prompt)
    memory.add_message("assistant", response.content)
    
    return {
        "messages": messages + [response],
        "current_agent": "coordinator_agent",
        "needs_user_input": True,
        "conversation_stage": "aggregator"
    }
aggregator_tool = Tool(
    name="AggregatorAgent",
    func=aggregator_agent,
    description="Use this to aggregate a given text. Input must be a text."
)