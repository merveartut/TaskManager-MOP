import { CirclePlus } from "lucide-react";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Modal } from "../../components/Modal/Modal";
import { Form } from "../../components/Form/Form";
import { projectStatusColors } from "../../constants/uiColors";
import { Input } from "../../components/Input/Input";
import { Select } from "../../components/Select/Select";
import { toast } from "react-hot-toast";
import { Divider } from "@mui/material";

interface Project {
  id: string;
  title: string;
  description: string;
  departmentName: string;
  teamMembers: string;
  status?: string;
}
const API_BASE = import.meta.env.VITE_API_URL;

export const Projects: React.FC = () => {
  const [projects, setProjects] = useState<Project[]>([]);
  const [users, setUsers] = useState<any[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [searchByTitle, setSearchByTitle] = useState("");
  const [isFetchingProjects, setIsFetchingProjects] = useState(false);
  const [isCreatingProjects, setIsCreatingProject] = useState(false);
  const [selectedProjectStatus, setSelectedProjectStatus] =
    useState<string>("");
  const userAdminOrGuest =
    localStorage.getItem("userRole") === "ADMIN" ||
    localStorage.getItem("userRole") === "GUEST";
  const navigate = useNavigate();

  const modalFields = [
    { name: "title", label: "Title", type: "text", visible: true },
    { name: "description", label: "Description", type: "text", visible: true },
    {
      name: "departmentName",
      label: "Department Name",
      type: "text",
      visible: true,
    },
    {
      name: "projectManager",
      label: "Project Manager",
      type: "picker",
      visible: true,
      isSingleSelect: true,
      options: users.filter((user) => user.role === "PROJECT_MANAGER"),
    },
    {
      name: "teamMembers",
      label: "Team Members",
      type: "picker",
      visible: true,
      options: users,
    }, // Pass users as options
  ];

  const checkAuthToken = (response: Response) => {
    if (response.status === 401) {
      localStorage.removeItem("token"); // Remove invalid token
      navigate("/login");
      return true; // Return true if the user should be logged out
    }
    return false; // Return false if everything is fine
  };

  useEffect(() => {
    const token = localStorage.getItem("token");
    const isGuest = localStorage.getItem("guest") === "true";
    if (!token && !isGuest) {
      alert("You should have a token or you should be GUEST!");
      navigate("/login");
      return;
    }

    const fetchProjects = async () => {
      try {
        setIsFetchingProjects(true);
        const response = await fetch(`${API_BASE}/api/projects/v1`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });
        if (checkAuthToken(response)) return;

        if (!response.ok) {
          throw new Error("Failed to fetch projects");
        }

        const data = await response.json();
        //setLoading(false);
        setProjects(data);
      } catch (error) {
        console.error("Error fetching projects:", error);
        alert("Error loading projects");
      } finally {
        setIsFetchingProjects(false);
      }
    };

    const fetchUsers = async () => {
      try {
        const response = await fetch(`${API_BASE}/api/users/v1`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });
        if (checkAuthToken(response)) return;

        if (!response.ok) {
          throw new Error("Failed to fetch users");
        }

        const data = await response.json();
        setUsers(data);
      } catch (error) {
        console.error("Error fetching users:", error);
      }
    };

    fetchProjects();
    fetchUsers();
  }, [navigate]);

  const handleCreateProject = async (formData: Record<string, any>) => {
    const token = localStorage.getItem("token");
    try {
      setIsCreatingProject(true);
      const response = await fetch(`${API_BASE}/api/projects/v1`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });
      if (!response.ok) {
        throw new Error("Failed to create project");
      }

      const newProject = await response.json();
      setProjects([...projects, newProject]); // Update project list
      setIsModalOpen(false); // Close modal
      toast.success("Project created successfully!");
    } catch (error) {
      console.error("Error creating project:", error);
      alert("Error creating project");
    } finally {
      setIsCreatingProject(false);
    }
  };

  const filteredProjects = projects.filter((project) => {
    return (
      project.title.toLowerCase().includes(searchByTitle.toLowerCase()) &&
      project.status &&
      (project.status.toString().includes(selectedProjectStatus) ||
        selectedProjectStatus === "ALL")
    );
  });
  return (
    <div className="w-full h-screen flex flex-col">
      <div className="flex flex-row justify-between items-center p-8 gap-6 flex-wrap w-full">
        <h1 className="text-[24px] font-bold md:px-8 font-roboto">Projects</h1>
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
            options={["ALL", "IN_PROGRESS", "CANCELLED", "COMPLETED"]}
            selectedValues={selectedProjectStatus}
            displayLabel={false}
            onChange={(selected: any) => setSelectedProjectStatus(selected)}
            isSingleSelect={true}
            customClass="bg-white rounded-md w-[200px]"
            label="Filter By State"
          ></Select>
        </div>
      </div>

      <Divider />

      {isFetchingProjects ? (
        <div className="flex justify-center items-center flex-grow h-[60vh]">
          <div className="animate-spin rounded-full h-10 w-10 border-t-4 border-b-4 border-purple-600"></div>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-6 p-8">
            {userAdminOrGuest && (
              <div
                onClick={() => setIsModalOpen(true)}
                className="flex flex-col items-center justify-center bg-white p-4 rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer"
              >
                <h2 className="text-xl font-semibold mb-6">
                  Create new Project
                </h2>
                <CirclePlus size={36} />
              </div>
            )}

            {filteredProjects.map((project: any) => (
              <div
                key={project.id}
                className="bg-white flex flex-col items-center p-4 gap-4 rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer"
                onClick={() => navigate(`/projectDetail/${project.id}`)}
              >
                <h2 className="text-xl font-semibold">{project.title}</h2>
                <p className="text-gray-600">
                  Department: {project.departmentName}
                </p>
                <div
                  className={`font-roboto font-bold p-2 w-fit rounded-md ${
                    projectStatusColors[project?.status] || ""
                  }`}
                >
                  {project && project.status}
                </div>
              </div>
            ))}
          </div>
        </>
      )}

      {/* Modal for Creating Project */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="CREATE NEW PROJECT"
      >
        <Form
          fields={modalFields}
          onSubmit={handleCreateProject}
          loading={isCreatingProjects}
        />
      </Modal>
    </div>
  );
};
