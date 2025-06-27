import { useNavigate, useParams } from "react-router-dom";
import {
  changePassword,
  fetchProjectsByTeamMember,
  fetchTasksByUserId,
  getUserById,
  updateUserEmail,
  updateUserName,
} from "../../services/projectApi";
import { useEffect, useState } from "react";
import { Divider } from "@mui/material";
import { Input } from "../../components/Input/Input";
import { Select } from "../../components/Select/Select";
import { stateColors } from "../../constants/uiColors";
import { Modal } from "../../components/Modal/Modal";
import { Form } from "../../components/Form/Form";
import toast from "react-hot-toast";
import { SquarePen } from "lucide-react";
import { TooltipHint } from "../../components/Tooltip/TooltipHint";

export const ProfilePage = () => {
  const { id } = useParams();
  const [user, setUser] = useState<any>();
  const [tasks, setTasks] = useState([]);
  const [projects, setProjects] = useState([]);
  const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false);
  const [isEmailModalOpen, setIsEmailModalOpen] = useState(false);
  const [isNameModalOpen, setIsNameModalOpen] = useState(false);
  const [emailInput, setEmailInput] = useState("");
  const [nameInput, setNameInput] = useState("");
  const navigate = useNavigate();
  const [searchByTaskTitle, setSearchByTaskTitle] = useState("");
  const [searchByProjectTitle, setSearchByProjectTitle] = useState("");
  const [selectedProjectStatus, setSelectedProjectStatus] =
    useState<string>("");
  const [selectedTaskState, setSelectedTaskState] = useState<string>("");
  const [isFetchingData, setIsFetchingData] = useState(false);
  const [isUpdatingPassword, setIsUpdatingPassword] = useState(false);
  const [isUpdatingName, setIsUpdatingName] = useState(false);
  const [isUpdatingEmail, setIsUpdatingEmail] = useState(false);

  const modalFields = [
    {
      name: "oldPassword",
      label: "Current Password",
      type: "password",
      visible: true,
    },
    {
      name: "newPassword",
      label: "New Password",
      type: "password",
      visible: true,
    },
  ];

  useEffect(() => {
    if (!id) return;

    const loadData = async () => {
      try {
        setIsFetchingData(true);
        const [userData, tasksData, projectsData] = await Promise.all([
          getUserById(id, navigate),
          fetchTasksByUserId(id, navigate),
          fetchProjectsByTeamMember(id, navigate),
        ]);

        setUser(userData);
        setTasks(tasksData);
        setProjects(projectsData);
        setEmailInput(userData.email);
        setNameInput(userData.name);
      } catch (error) {
        console.error("Error loading data:", error);
        alert("Error loading data");
      } finally {
        setIsFetchingData(false);
      }
    };

    loadData();
  }, [id]);

  const filteredTasks = tasks.filter((task: any) => {
    return (
      task.title.toLowerCase().includes(searchByTaskTitle.toLowerCase()) &&
      task.state &&
      (task.state.toString().includes(selectedTaskState) ||
        selectedTaskState === "ALL")
    );
  });

  const filteredProjects = projects.filter((project: any) => {
    return (
      project.title
        .toLowerCase()
        .includes(searchByProjectTitle.toLowerCase()) &&
      project.status &&
      (project.status.toString().includes(selectedProjectStatus) ||
        selectedProjectStatus === "ALL")
    );
  });

  const handleChangePassword = async (formData: any) => {
    try {
      setIsUpdatingPassword(true);
      await changePassword(formData);
      toast.success("Password updated successfully!");
      setIsPasswordModalOpen(false);
    } catch (error: any) {
      alert(error.message || "Failed to change password");
    } finally {
      setIsUpdatingPassword(false);
    }
  };

  const handleChangeEmail = async () => {
    if (id) {
      try {
        setIsUpdatingEmail(true);
        await updateUserEmail(id, emailInput, navigate);
        toast.success("Email updated successfully!");
        setIsEmailModalOpen(false);
      } catch (error: any) {
        alert(error.message || "Failed to update email");
      } finally {
        setIsUpdatingEmail(false);
      }
    }
  };

  const handleChangeName = async () => {
    if (id) {
      try {
        setIsUpdatingName(true);
        await updateUserName(id, nameInput, navigate);
        toast.success("Name updated successfully!");
        setIsNameModalOpen(false);
      } catch (error: any) {
        alert(error.message || "Failed to update name");
      } finally {
        setIsUpdatingName(false);
      }
    }
  };
  return (
    <div className="w-full h-full flex flex-col">
      <div className="flex flex-col items-start justify-start md:p-8 lg:p-8">
        <div className="flex flex-row align-middle gap-2">
          <div className="rounded-lg px-8 py-4 items-center flex rounded-b-none align-middle gap-2">
            <h1 className="text-[24px] font-bold font-roboto">
              {user && user.name}
            </h1>
            <TooltipHint text="Update Name">
              <button onClick={() => setIsNameModalOpen(true)}>
                <SquarePen size={18} />
              </button>
            </TooltipHint>
          </div>
        </div>
        <div className="flex flex-row w-full md:gap-[160px] lg:gap-[160px] align-middle items-center flex-wrap">
          <div className="flex flex-row items-center align-middle px-8 py-4 gap-6">
            <h2 className="text-lg font-roboto">Role:</h2>
            <span className="font-roboto font-bold">{user && user.role}</span>
          </div>
          <div className="flex flex-row items-center align-middle px-8 py-4 gap-6">
            <h2 className="text-lg font-roboto">E-mail:</h2>
            <span className="font-roboto font-bold">{user && user.email}</span>
            <TooltipHint text="Update Email">
              <button onClick={() => setIsEmailModalOpen(true)}>
                <SquarePen size={18} />
              </button>
            </TooltipHint>
          </div>
          <div className="px-8 py-4">
            <button
              className="bg-blue-700 text-white rounded hover:bg-blue-600 md:px-4 px-2 py-2 md:text-md text-sm"
              onClick={() => setIsPasswordModalOpen(true)}
            >
              Change Password
            </button>
          </div>
        </div>
      </div>

      {projects.length === 0 ? (
        <div className="p-8 gap-8 flex flex-col">
          <Divider /> <span>There is no projects data</span>
        </div>
      ) : (
        <>
          <Divider />
          <div className="flex flex-row justify-between items-center p-8 flex-wrap gap-4">
            <h1 className="text-[24px] font-bold md:px-8 font-roboto">
              Projects
            </h1>
            <div className="flex flex-row gap-4 flex-wrap">
              <Input
                label="Search by title"
                type="text"
                name="title"
                variant="outlined"
                customClass="bg-white rounded-md"
                value={searchByProjectTitle}
                onChange={(e) => setSearchByProjectTitle(e.target.value)}
              ></Input>
              <Select
                options={["ALL", "IN_PROGRESS", "CANCELLED", "COMPLETED"]}
                selectedValues={selectedProjectStatus}
                displayLabel={false}
                onChange={(selected: any) => setSelectedProjectStatus(selected)}
                isSingleSelect={true}
                customClass="bg-white rounded-md w-[200px]"
                label="Filter By Status"
              ></Select>
            </div>
          </div>
        </>
      )}
      {isFetchingData ? (
        <div className="flex justify-center items-center flex-grow h-[60vh]">
          <div className="animate-spin rounded-full h-10 w-10 border-t-4 border-b-4 border-purple-600"></div>
        </div>
      ) : (
        projects.length > 0 && (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-6 p-8">
            {filteredProjects.map((project: any) => (
              <div
                key={project.id}
                className="bg-white flex flex-col p-4 items-center rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer"
                onClick={() => navigate(`/projectDetail/${project.id}`)}
              >
                <h2 className="text-xl font-roboto font-semibold">
                  {project.title}
                </h2>
                <div
                  className={`text-gray-600 mt-4 font-roboto font-bold w-fit p-2 rounded-md ${
                    stateColors[project.status] || ""
                  }`}
                >
                  {project.status}
                </div>
              </div>
            ))}
          </div>
        )
      )}

      {<Divider />}

      <div className="flex flex-row justify-between items-center p-8 flex-wrap">
        <h1 className="text-[24px] font-bold md:px-8 font-roboto">Tasks</h1>

        {filteredTasks.length === 0 ? (
          <div className="p-8 w-full">
            <span>There is no tasks data</span>
          </div>
        ) : (
          <div className="flex flex-row gap-4">
            <Input
              label="Search by title"
              type="text"
              name="title"
              variant="outlined"
              customClass="bg-white rounded-md"
              value={searchByTaskTitle}
              onChange={(e) => setSearchByTaskTitle(e.target.value)}
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
          </div>
        )}
      </div>
      {isFetchingData ? (
        <div className="flex justify-center items-center flex-grow h-[60vh]">
          <div className="animate-spin rounded-full h-10 w-10 border-t-4 border-b-4 border-purple-600"></div>
        </div>
      ) : (
        filteredTasks.length > 0 && (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-6 p-8">
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
        )
      )}

      <Modal
        isOpen={isPasswordModalOpen}
        onClose={() => setIsPasswordModalOpen(false)}
        title="CHANGE PASSWORD"
      >
        <Form
          fields={modalFields}
          onSubmit={handleChangePassword}
          loading={isUpdatingPassword}
        />
      </Modal>
      <Modal
        isOpen={isEmailModalOpen}
        onClose={() => setIsEmailModalOpen(false)}
        title="CHANGE EMAIL"
      >
        <Input
          label="Email"
          type="text"
          name="email"
          variant="outlined"
          customClass="bg-white rounded-md"
          value={emailInput}
          onChange={(e) => setEmailInput(e.target.value)}
        ></Input>
        <div className="flex flex-row justify-center gap-6">
          <button
            className="bg-blue-600 text-white rounded px-4 py-2 mt-4 self-end flex items-center justify-center min-w-[100px]"
            disabled={isUpdatingEmail}
            onClick={handleChangeEmail}
          >
            {isUpdatingEmail ? (
              <span className="inline-block animate-spin rounded-full h-5 w-5 border-t-2 border-white border-solid"></span>
            ) : (
              "Submit"
            )}
          </button>
        </div>
      </Modal>
      <Modal
        isOpen={isNameModalOpen}
        onClose={() => setIsNameModalOpen(false)}
        title="CHANGE NAME"
      >
        <Input
          label="Name"
          type="text"
          name="name"
          variant="outlined"
          customClass="bg-white rounded-md"
          value={nameInput}
          onChange={(e) => setNameInput(e.target.value)}
        ></Input>
        <div className="flex flex-row justify-center gap-6">
          <button
            className="bg-blue-600 text-white rounded px-4 py-2 mt-4 flex items-center justify-center min-w-[100px]"
            disabled={isUpdatingName}
            onClick={handleChangeName}
          >
            {isUpdatingName ? (
              <span className="inline-block animate-spin rounded-full h-5 w-5 border-t-2 border-white border-solid"></span>
            ) : (
              "Submit"
            )}
          </button>
        </div>
      </Modal>
    </div>
  );
};
