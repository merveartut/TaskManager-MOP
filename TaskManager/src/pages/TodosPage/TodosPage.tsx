import { Checkbox } from "@mui/material";
import { Circle, CircleCheckBig, CirclePlus, Pin } from "lucide-react";
import React, { useEffect, useState } from "react";
import { Modal } from "../../components/Modal/Modal";
import {
  createTodo,
  fetchTodos,
  updateTodoState,
} from "../../services/projectApi";
import { useNavigate } from "react-router-dom";
import { TooltipHint } from "../../components/Tooltip/TooltipHint";

interface TodosProps {
  taskId: string;
  hasAuth?: boolean;
}

export const TodosPage: React.FC<TodosProps> = ({
  taskId,
  hasAuth = false,
}) => {
  const [isTodoModalOpen, setIsTodoModalOpen] = useState(false);
  const [todoText, setTodoText] = useState("");
  const [todos, setTodos] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    if (!taskId) return;

    const loadData = async () => {
      try {
        const [todoData] = await Promise.all([fetchTodos(taskId, navigate)]);

        setTodos(todoData);
      } catch (error) {
        console.error("Error loading data:", error);
        alert("Error loading data");
      }
    };

    loadData();
  }, [taskId]);

  const handleChangeChecked = async (id: string, state: boolean) => {
    if (!id) return;

    try {
      const updatedTodo = await updateTodoState(id, state, navigate);
      // @ts-ignore
      setTodos((prev) =>
        prev.map((t: any) => (t.id === updatedTodo.id ? updatedTodo : t))
      );
    } catch (error) {
      console.error("Error updating todo state", error);
      alert("Error updating todo state");
    }
  };

  const handleAddTodo = async () => {
    console.log("bu mu ?");
    if (!todoText || !todoText.trim()) return;

    const fullData = {
      text: todoText,
      task: { id: taskId },
      completedState: false,
    };

    try {
      const newTodo = await createTodo(fullData, navigate);
      if (newTodo) {
        // @ts-ignore
        setTodos((prev) => [...prev, newTodo]);
        setTodoText("");
        setIsTodoModalOpen(false);
      }
    } catch (error) {
      console.error("Error creating todo:", error);
      alert("Error creating todo");
    }
  };
  return (
    <div className="flex flex-col p-8 gap-6 bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow border-2">
      <div className="flex flex-row align-middle items-center">
        <Pin className="rotate-45" />
        <div className="flex justify-center w-full">
          <h1 className="text-lg font-bold  font-roboto">Todos</h1>
        </div>
        {hasAuth && (
          <TooltipHint text="Add new todo item">
            <button
              className="p-2 text-blue-600 hover:text-blue-800"
              onClick={() => setIsTodoModalOpen(true)}
            >
              <CirclePlus size={32} />
            </button>
          </TooltipHint>
        )}
      </div>

      <ul>
        {todos.map((todo: any, index) => (
          <li key={index}>
            <div className="flex flex-row justify-between align-middle items-center">
              <div className="flex align-middle h-[32px]">{todo.text}</div>
              <Checkbox
                checked={todo.completedState}
                disabled={!hasAuth}
                onChange={() =>
                  handleChangeChecked(todo.id, !todo.completedState)
                }
                icon={
                  <span className="w-[32px] h-[32px]">
                    <Circle className="w-full h-full" />
                  </span>
                }
                checkedIcon={
                  <span className="w-[32px] h-[32px]">
                    <CircleCheckBig className="w-full h-full" />
                  </span>
                }
              />
            </div>
          </li>
        ))}
      </ul>
      <Modal
        isOpen={isTodoModalOpen}
        onClose={() => setIsTodoModalOpen(false)}
        title="ADD NEW TODO"
      >
        <div className="flex flex-col gap-4">
          <textarea
            className="p-2 w-full focus:border-slate-500 focus:ring-1 focus:ring-slate-300"
            rows={4}
            placeholder="Enter your new todo ..."
            value={todoText}
            onChange={(e) => setTodoText(e.target.value)}
          />
          <button
            className="bg-blue-500 text-white rounded px-4 py-2 self-end cursor-pointer hover:bg-blue-700"
            onClick={handleAddTodo}
            disabled={!todoText.trim()}
          >
            Submit
          </button>
        </div>
      </Modal>
    </div>
  );
};
