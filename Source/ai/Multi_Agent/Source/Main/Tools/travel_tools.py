from langchain_community.chat_models import ollama, ChatOllama
from langchain.tools import Tool
from langchain.agents import create_react_agent, AgentExecutor
from langchain import hub
from typing import TypedDict, Annotated, List
import operator

llm = ChatOllama(model="llama3")
prompt = hub.pull("hwchase17/react")

class AgentState(TypedDict):
    input: str
    message: Annotated[List[str], operator.add]
    
def get_answer_travel(travel: str) -> str:
    travel = llm.invoke(f"")