import { CirclePlus, Delete } from "lucide-react";
import { useEffect, useState } from "react";
import { Modal } from "../../components/Modal/Modal";
import { Form } from "../../components/Form/Form";
import { createUser, deleteUser, fetchUsers } from "../../services/projectApi";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { Select } from "../../components/Select/Select";
import { TooltipHint } from "../../components/Tooltip/TooltipHint";

export const SettingsPage = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isDeleteConfirmModalOpen, setIsDeleteConfirmModalOpen] =
    useState(false);
  const [selectedUser, setSelectedUser] = useState<any>();
  const [users, setUsers] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const loadData = async () => {
      try {
        const [usersData] = await Promise.all([fetchUsers(navigate)]);

        setUsers(usersData);
      } catch (error) {
        console.error("Error loading data:", error);
        alert("Error loading data");
      }
    };

    loadData();
  }, [navigate]);

  const modalFields: any = [
    { name: "name", label: "Name", type: "text", visible: true },
    { name: "password", label: "Password", type: "text", visible: true },
    {
      name: "role",
      label: "Role",
      type: "picker",
      visible: true,
      isSingleSelect: true,
      options: ["ADMIN", "PROJECT_MANAGER", "TEAM_LEADER", "TEAM_MEMBER"],
    },
  ];

  const handleCreateUser = async (formData: any) => {
    try {
      const newUser = await createUser(formData, navigate);
      if (newUser) {
        setIsModalOpen(false);
        toast.success("User added successfully!");
        const updatedUsers = await fetchUsers(navigate);
        setUsers(updatedUsers);
      }
    } catch (error) {
      console.error("Error creating user:", error);
      alert("Error creating user");
    }
  };

  const handleDeleteUser = async () => {
    try {
      await deleteUser(selectedUser.id, navigate);
      setIsDeleteConfirmModalOpen(false);
      setIsDeleteModalOpen(false);
      setSelectedUser(null);
      toast.success("User deleted successfully!");
    } catch (error) {
      console.error("Error deleting user:", error);
      alert("Error deleting user");
      setSelectedUser(null);
      setIsDeleteConfirmModalOpen(false);
      setIsDeleteModalOpen(false);
    }
  };
  return (
    <div className="flex flex-col gap-6 p-8 justify-start items-center w-full mt-20">
      <div className=" overflow-y-auto">
        <div className="grid  md:grid-cols-4 gap-12 lg:grid-cols-4 grid-cols-3 font-semibold bg-gray-100 p-4 rounded-t-lg">
          <div className="md:block lg:block hidden">ID</div>
          <div>Name</div>
          <div>Role</div>
        </div>
        <div
          onClick={() => setIsModalOpen(true)}
          className="flex flex-row gap-6 items-center bg-white p-4 border-t border-gray-200 hover:shadow transition-shadow cursor-pointer"
        >
          <CirclePlus className="text-green-700 md:w-[24px] lg:w-[24px] w-[18px]" />
          <span className="font-semibold text-green-700 md:text-md lg:text-md text-sm">
            {" "}
            Add New User
          </span>
        </div>
        {users.map((user: any) => (
          <div
            key={user.id}
            className="grid md:grid-cols-4 gap-12 lg:grid-cols-4 grid-cols-3 items-center bg-white p-4 border-t border-gray-200 hover:shadow-2xl transition-shadow cursor-pointer"
          >
            <div className="truncate md:block lg:block hidden">{user.id}</div>
            <div className="md:text-md lg:text-md text-sm">{user.name}</div>
            <div className="md:text-md lg:text-md text-sm">{user.role}</div>
            <div>
              <TooltipHint text="Delete User">
                <button
                  onClick={() => setIsDeleteModalOpen(true)}
                  className="cursor-pointer hover:text-indigo-700 "
                >
                  <Delete className="md:w-[24px] lg:w-[24px] w-[18px]" />
                </button>
              </TooltipHint>
            </div>
          </div>
        ))}
      </div>

      {/* Modal for Creating Task */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="CREATE NEW USER"
      >
        <Form fields={modalFields} onSubmit={handleCreateUser} />
      </Modal>
      <Modal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        title="DELETE USER"
      >
        <Select
          label="Select User"
          options={users}
          customClass="mb-6"
          displayLabel={true}
          isSingleSelect={true}
          selectedValues={selectedUser}
          onChange={(selected: any) => setSelectedUser(selected)}
        />
        <button
          type="submit"
          className="mt-4 bg-blue-700 text-white py-2 px-4 rounded"
          onClick={() => setIsDeleteConfirmModalOpen(true)}
        >
          Submit
        </button>
      </Modal>
      <Modal
        isOpen={isDeleteConfirmModalOpen}
        onClose={() => setIsDeleteConfirmModalOpen(false)}
        title="DELETE USER"
      >
        <span>Are you sure you want to delete this user ?</span>
        <div className="flex flex-row justify-end gap-6">
          <button
            type="submit"
            className="mt-4 bg-slate-500 text-white py-2 px-4 rounded"
            onClick={() => setIsDeleteConfirmModalOpen(false)}
          >
            No
          </button>
          <button
            type="submit"
            className="mt-4 bg-blue-700 text-white py-2 px-4 rounded"
            onClick={handleDeleteUser}
          >
            Yes
          </button>
        </div>
      </Modal>
    </div>
  );
};
