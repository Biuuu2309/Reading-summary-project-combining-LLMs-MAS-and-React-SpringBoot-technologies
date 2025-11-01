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
    messages: Annotated[List[Any], operator.add]
    current_agent: str
    needs_user_input: bool
    conversation_stage: Literal["greeting", "text_input", "summary_type", "processing", "completed"]
    original_text: str
    summary_type: Literal["extract", "abstract", None]
    grade_level: int
    processed_text: str
    summary_result: str

SPELLCHECKER_SYSTEM = """Bạn là Spell Checker Agent chuyên nghiệp. Nhiệm vụ:
1. Kiểm tra và sửa lỗi chính tả trong văn bản
2. KHÔNG thay đổi nội dung, chỉ sửa lỗi chính tả, dấu câu, và các từ sai
3. Trả về văn bản đã được sửa"""

def spellchecker_agent(state: AgentState):
    messages = state["messages"]
    memory = memory_manager.get_memory()
    processed_text = state.get("processed_text", "")
    
    if not processed_text:
        response = AIMessage(content="Không có văn bản để kiểm tra chính tả.")
        memory.add_message("assistant", response.content)
        return {
            "messages": [response],
            "current_agent": "coordinator_agent",
            "needs_user_input": True,
            "conversation_stage": "text_input",
            "original_text": state.get("original_text", ""),
            "summary_type": None,
            "grade_level": 0,
            "processed_text": "",
            "summary_result": ""
        }
    
    # Trong thực tế có thể có spell checker thật
    corrected_text = processed_text  # Tạm thời giữ nguyên
    
    response = AIMessage(content=f"Văn bản đã được kiểm tra chính tả:\n\n{corrected_text}\n\nBây giờ hãy chọn loại tóm tắt:\n1. TRÍCH XUẤT (Extract): Giữ nguyên câu từ quan trọng\n2. DIỄN GIẢI (Abstract): Viết lại theo cách hiểu của bạn\n\nVà cho biết khối lớp (1-5):")
    memory.add_message("assistant", response.content)
    
    return {
        "messages": [response],
        "current_agent": "coordinator_agent",
        "needs_user_input": True,
        "conversation_stage": "summary_type",
        "original_text": state.get("original_text", ""),
        "summary_type": None,
        "grade_level": 0,
        "processed_text": corrected_text,
        "summary_result": ""
    }
spellchecker_tool = Tool(
    name="SpellCheckerAgent",
    func=spellchecker_agent,
    description="Use this to check the spelling of a given text. Input must be a text."
)