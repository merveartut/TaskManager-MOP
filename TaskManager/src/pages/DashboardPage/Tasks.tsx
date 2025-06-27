import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  createTask,
  fetchProjects,
  fetchTasks,
  fetchUsers,
} from "../../services/projectApi";
import { stateColors } from "../../constants/uiColors";
import { Input } from "../../components/Input/Input";
import { Select } from "../../components/Select/Select";
import { CirclePlus } from "lucide-react";
import { Modal } from "../../components/Modal/Modal";
import { Form } from "../../components/Form/Form";
import { Divider } from "@mui/material";

export const Tasks = () => {
  const navigate = useNavigate();
  const [tasks, setTasks] = useState([]);
  const [projects, setProjects] = useState([]);
  const [users, setUsers] = useState([]);
  const [searchByTitle, setSearchByTitle] = useState("");
  const [selectedTaskState, setSelectedTaskState] = useState<string>("");
  const [selectedUser, setSelectedUser] = useState<string | any>("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isFetchingData, setIsFetchingData] = useState(false);
  const [isCreatingTask, setIsCreatingTask] = useState(false);

  const userId = localStorage.getItem("userId");
  const userRole = localStorage.getItem("userRole");

  const filteredProjects = projects.filter((project: any) => {
    if (userRole === "ADMIN" || userRole === "GUEST") {
      return project;
    }
    if (userRole === "PROJECT_MANAGER") {
      return project.projectManager.id === userId;
    }
  });

  const modalFields: any = [
    { name: "title", label: "Title", type: "text", visible: true },
    { name: "description", label: "Description", type: "text", visible: true },
    {
      name: "project",
      label: "Project",
      type: "picker",
      visible: true,
      isSingleSelect: true,
      options: filteredProjects,
    },
    {
      name: "priority",
      label: "Priority",
      type: "picker",
      visible: true,
      isSingleSelect: true,
      options: ["CRITICAL", "HIGH", "MEDIUM", "LOW"],
    },
    {
      name: "assignee",
      label: "Assignee",
      type: "picker",
      visible: true,
      isSingleSelect: true,
      options: users,
    }, // Pass users as options
  ];

  useEffect(() => {
    const loadData = async () => {
      try {
        setIsFetchingData(true);
        const [projectsData, tasksData, usersData] = await Promise.all([
          fetchProjects(navigate),
          fetchTasks(navigate),
          fetchUsers(navigate),
        ]);

        setProjects(projectsData);
        setTasks(tasksData);
        setUsers(usersData);
      } catch (error) {
        console.error("Error loading data:", error);
        alert("Error loading data");
      } finally {
        setIsFetchingData(false);
      }
    };

    loadData();
  }, [navigate]);

  const handleCreateTask = async (formData: any) => {
    try {
      setIsCreatingTask(true);
      const newTask = await createTask(formData, navigate);
      if (newTask) {
        // @ts-ignore
        setTasks((prev) => [...prev, newTask]);
        setIsModalOpen(false);
      }
    } catch (error) {
      console.error("Error creating task:", error);
      alert("Error creating task");
    } finally {
      setIsCreatingTask(false);
    }
  };

  const filteredTasks = tasks.filter((task: any) => {
    return (
      task.title.toLowerCase().includes(searchByTitle.toLowerCase()) &&
      task.state &&
      (task.state.toString().includes(selectedTaskState) ||
        selectedTaskState === "ALL") &&
      task.assignee.name &&
      (task.assignee.name
        .toString()
        .toLowerCase()
        .includes(selectedUser && selectedUser.name.toString().toLowerCase()) ||
        selectedUser === null)
    );
  });

  return (
    <div className="w-full h-full flex  flex-col">
      <div className="flex flex-row justify-between items-center p-8 flex-wrap gap-6">
        <h1 className="text-[24px] font-bold px-8 font-roboto">Tasks</h1>
        <div className="flex flex-row gap-4 flex-wrap">
          <Input
            label="Search by title"
            type="text"
            name="title"
            variant="outlined"
            customClass="bg-white rounded-md"
            value={searchByTitle}
            onChange={(e) => setSearchByTitle(e.target.value)}
          ></Input>
          <Select
            options={[
              "ALL",
              "BACKLOG",
              "IN_ANALYSIS",
              "IN_DEVELOPMENT",
              "CANCELLED",
              "BLOCKED",
              "COMPLETED",
            ]}
            selectedValues={selectedTaskState}
            displayLabel={false}
            onChange={(selected: any) => setSelectedTaskState(selected)}
            isSingleSelect={true}
            customClass="bg-white rounded-md w-[200px]"
            label="Filter By State"
          ></Select>
          <Select
            options={users}
            selectedValues={selectedUser}
            displayLabel={false}
            displayClearButtonForSingleSelect={true}
            clearSelected={() => setSelectedUser(null)}
            onChange={(selected) => setSelectedUser(selected)}
            isSingleSelect={true}
            customClass="bg-white rounded-md w-[200px]"
            label="Filter By Assignee"
          ></Select>
        </div>
      </div>

      <Divider />

      {isFetchingData ? (
        <div className="flex justify-center items-center flex-grow h-[60vh]">
          <div className="animate-spin rounded-full h-10 w-10 border-t-4 border-b-4 border-purple-600"></div>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-6 p-8">
          {(userRole === "ADMIN" ||
            userRole === "PROJECT_MANAGER" ||
            userRole === "GUEST") && (
            <div
              onClick={() => setIsModalOpen(true)}
              className="flex flex-col items-center justify-center bg-white p-4 rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer"
            >
              <h2 className="text-xl font-roboto font-semibold mb-6">
                Create new Task
              </h2>
              <CirclePlus size={36} />
            </div>
          )}
          {filteredTasks.map((task: any) => (
            <div
              key={task.id}
              className="bg-white flex flex-col p-4 items-center rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer"
              onClick={() => navigate(`/taskDetail/${task.id}`)}
            >
              <h2 className="text-xl font-roboto font-semibold">
                {task.title}
              </h2>
              <p className="text-gray-600 font-roboto mt-4">
                Project: {task.project.title}
              </p>
              <p className="text-gray-600 font-roboto mt-4">
                Assignee: {task.assignee.name}
              </p>
              <div
                className={`text-gray-600 mt-4 font-roboto font-bold w-fit p-2 rounded-md ${
                  stateColors[task.state] || ""
                }`}
              >
                {task.state}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Modal for Creating Task */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="CREATE NEW TASK"
      >
        <Form
          fields={modalFields}
          onSubmit={handleCreateTask}
          loading={isCreatingTask}
        />
      </Modal>
    </div>
  );
};
