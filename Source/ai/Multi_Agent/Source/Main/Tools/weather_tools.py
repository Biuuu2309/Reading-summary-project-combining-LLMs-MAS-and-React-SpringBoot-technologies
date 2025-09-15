from langchain_community.chat_models import ollama, ChatOllama
from langchain.tools import Tool
from langchain.agents import create_react_agent, AgentExecutor
from langchain import hub
from typing import TypedDict, Annotated, List
import operator
from dotenv import load_dotenv
import requests

load_dotenv()

llm = ChatOllama(model="llama3") # <-- Sử dụng model bạn đã kéo về, ví dụ "llama3", "mistral"
prompt = hub.pull("hwchase17/react")

class AgentState(TypedDict):
    # Thông điệp của người dùng
    input: str
    # Đầu ra của từng agent sẽ được nối vào đây
    messages: Annotated[List[str], operator.add]
    
def get_weather_llm(city: str) -> str:
    """Lấy thông tin thời tiết (mô phỏng bằng LLM)."""
    weather = llm.invoke(f"Hãy mô tả ngắn gọn tình hình thời tiết hiện tại ở {city}.")
    return weather.content

weatherllm_tool = Tool(
    name="WeatherLLM",
    func=get_weather_llm,
    description="Use this to get current weather for a given city. Input must be a city name."
)

def get_current_weather(city: str) -> str:
    """Lấy thời tiết hiện tại của một thành phố từ OpenWeatherMap"""
    url = f"http://api.openweathermap.org/data/2.5/weather?q={city}&appid={OPENWEATHER_API_KEY}&lang=vi&units=metric"
    response = requests.get(url)
    data = response.json()

    if data.get("cod") != 200:
        return f"Không tìm thấy dữ liệu thời tiết cho {city}."

    temp = data["main"]["temp"]
    desc = data["weather"][0]["description"]
    return f"Thời tiết tại {city} hiện tại: {desc}, nhiệt độ {temp}°C."

weatherapi_tool = Tool(
    name="WeatherAPI",
    func=get_current_weather,
    description="Dùng để lấy thời tiết hiện tại cho một thành phố. Input là tên thành phố."
)