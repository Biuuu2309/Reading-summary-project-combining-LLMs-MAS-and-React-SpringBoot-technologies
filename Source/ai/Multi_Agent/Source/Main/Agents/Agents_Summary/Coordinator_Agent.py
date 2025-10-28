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

COORDINATOR_SYSTEM = """Bạn là Coordinator Agent thông minh giúp học sinh tiểu học tóm tắt văn bản theo 2 cách (TRÍCH XUẤT và DIỄN GIẢI) phù hợp với khối lớp (1-5).

Workflow của bạn:
1. GREETING: Chào hỏi và yêu cầu user cung cấp văn bản
2. TEXT_INPUT: Nhận văn bản từ user và chuyển cho OCR/SpellChecker để xử lý
3. SUMMARY_TYPE: Hỏi user muốn tóm tắt TRÍCH XUẤT hay DIỄN GIẢI và khối lớp nào (1-5)
4. PROCESSING: Phân công cho agent phù hợp (Extractor hoặc Abstracter)
5. COMPLETED: Tổng hợp kết quả và hỏi đánh giá hệ thống

Luôn trả lời ngắn gọn và đi thẳng vào vấn đề."""

def coordinator_agent(state: AgentState):
    messages = state["messages"]
    memory = memory_manager.get_memory()
    conversation_stage = state.get("conversation_stage", "greeting")
    
    print(f"🔍 Coordinator Agent - Stage: {conversation_stage}, Messages: {len(messages)}")
    
    # Xử lý trường hợp messages rỗng - GREETING
    if not messages:
        response = AIMessage(content="Xin chào! Tôi là trợ lý tóm tắt thông minh cho học sinh tiểu học.\n\nHãy cung cấp văn bản bạn muốn tóm tắt:")
        memory.add_message("assistant", response.content)
        return {
            "messages": [response],
            "current_agent": "coordinator_agent",
            "needs_user_input": True,
            "conversation_stage": "text_input",
            "original_text": "",
            "summary_type": None,
            "grade_level": 0,
            "processed_text": "",
            "summary_result": "",
            "final_result": ""
        }
    
    last_message = messages[-1]
    
    if isinstance(last_message, HumanMessage):
        user_input = last_message.content
        memory.add_message("user", user_input)
        
        print(f"👤 User input: {user_input}")
        print(f"📊 Conversation stage: {conversation_stage}")
        
        # Xử lý theo từng giai đoạn
        if conversation_stage == "text_input":
            # Lưu văn bản gốc và chuyển sang xử lý OCR/SpellChecker
            response = AIMessage(content="Văn bản đã được nhận! Đang xử lý...")
            memory.add_message("assistant", response.content)
            return {
                "messages": [response],
                "current_agent": "reader_ocr_agent",
                "needs_user_input": False,
                "conversation_stage": "text_input",
                "original_text": user_input,
                "summary_type": None,
                "grade_level": 0,
                "processed_text": "",
                "summary_result": "",
                "final_result": ""
            }
            
        elif conversation_stage == "summary_type":
            # Phân tích yêu cầu về loại tóm tắt và khối lớp
            content = user_input.lower()
            if "trích xuất" in content or "extract" in content or "1" in content:
                summary_type = "extract"
            elif "diễn giải" in content or "abstract" in content or "2" in content:
                summary_type = "abstract"
            else:
                summary_type = "extract"  # Mặc định
            
            # Tìm khối lớp
            grade_level = 3  # Mặc định lớp 3
            for i in range(1, 6):
                if str(i) in content:
                    grade_level = i
                    break
            
            response = AIMessage(content=f"Đã xác nhận: Tóm tắt {summary_type} cho lớp {grade_level}. Đang xử lý...")
            memory.add_message("assistant", response.content)
            
            return {
                "messages": [response],
                "current_agent": "coordinator_agent",
                "needs_user_input": False,
                "conversation_stage": "processing",
                "original_text": state.get("original_text", ""),
                "summary_type": summary_type,
                "grade_level": grade_level,
                "processed_text": state.get("processed_text", ""),
                "summary_result": "",
                "final_result": ""
            }
            
        elif conversation_stage == "completed":
            # Xử lý đánh giá từ user
            if "tốt" in user_input.lower() or "hay" in user_input.lower() or "được" in user_input.lower():
                response = AIMessage(content="Cảm ơn bạn đã đánh giá tích cực! Hệ thống sẽ tiếp tục cải thiện.")
            else:
                response = AIMessage(content="Cảm ơn bạn đã đánh giá! Hệ thống sẽ tiếp tục cải thiện.")
            
            memory.add_message("assistant", response.content)
            return {
                "messages": [response],
                "current_agent": "coordinator_agent",
                "needs_user_input": True,
                "conversation_stage": "greeting",
                "original_text": "",
                "summary_type": None,
                "grade_level": 0,
                "processed_text": "",
                "summary_result": "",
                "final_result": ""
            }
    
    # Xử lý khi nhận kết quả từ Aggregator Agent
    elif conversation_stage == "processing" and state.get("final_result"):
        final_result = state.get("final_result", "")
        response = AIMessage(content=f"🎉 **KẾT QUẢ TÓM TẮT**\n\n{final_result}\n\n---\n\nBạn có hài lòng với bản tóm tắt này không? Hãy đánh giá hệ thống:")
        memory.add_message("assistant", response.content)
        return {
            "messages": [response],
            "current_agent": "coordinator_agent",
            "needs_user_input": True,
            "conversation_stage": "completed",
            "original_text": state.get("original_text", ""),
            "summary_type": state.get("summary_type", None),
            "grade_level": state.get("grade_level", 0),
            "processed_text": state.get("processed_text", ""),
            "summary_result": state.get("summary_result", ""),
            "final_result": final_result
        }
    
    # Trường hợp không phải HumanMessage
    return {
        "messages": [],
        "current_agent": "coordinator_agent",
        "needs_user_input": True,
        "conversation_stage": conversation_stage,
        "original_text": state.get("original_text", ""),
        "summary_type": state.get("summary_type", None),
        "grade_level": state.get("grade_level", 0),
        "processed_text": state.get("processed_text", ""),
        "summary_result": state.get("summary_result", ""),
        "final_result": state.get("final_result", "")
    }
    
coordinator_tool = Tool(
    name="CoordinatorAgent",
    func=coordinator_agent,
    description="Use this to get current coordinator for a given task. Input must be a task name."
)