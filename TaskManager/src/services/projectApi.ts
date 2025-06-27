const API_BASE = import.meta.env.VITE_API_URL;

const getAuthHeaders = () => ({
  Authorization: `Bearer ${localStorage.getItem("token")}`,
  "Content-Type": "application/json",
});

const handleAuthRedirect = (response: Response, navigate: any) => {
  if (response.status === 401) {
    localStorage.removeItem("token");
    navigate("/login");
    return true;
  }
  return false;
};

export const fetchProjectById = async (id: string, navigate: any) => {
  const response = await fetch(`${API_BASE}/api/projects/v1/${id}`, {
    headers: getAuthHeaders(),
  });
  if (handleAuthRedirect(response, navigate)) return null;
  if (!response.ok) throw new Error("Failed to fetch project");
  return response.json();
};

export const fetchUsers = async (navigate: any) => {
  const response = await fetch(`${API_BASE}/api/users/v1`, {
    headers: getAuthHeaders(),
  });
  if (handleAuthRedirect(response, navigate)) return [];
  if (!response.ok) throw new Error("Failed to fetch users");
  return response.json();
};

export const fetchTasksByProjectId = async (id: string, navigate: any) => {
  const response = await fetch(`${API_BASE}/api/tasks/v1/project/${id}`, {
    headers: getAuthHeaders(),
  });
  if (handleAuthRedirect(response, navigate)) return [];
  if (!response.ok) throw new Error("Failed to fetch tasks");
  return response.json();
};

export const getTaskById = async (id: string, navigate: any) => {
  const response = await fetch(`${API_BASE}/api/tasks/v1/task/${id}`, {
    headers: getAuthHeaders(),
  });
  if (handleAuthRedirect(response, navigate)) return [];
  if (!response.ok) throw new Error("Failed to get task");
  return response.json();
};

export const createTask = async (data: any, navigate: any) => {
  console.log("jdhjdfg", data);

  const response = await fetch(`${API_BASE}/api/tasks/v1`, {
    method: "POST",
    headers: getAuthHeaders(),
    body: JSON.stringify(data),
  });
  if (handleAuthRedirect(response, navigate)) return null;
  if (!response.ok) throw new Error("Failed to create task");
  return response.json();
};

export const createComment = async (data: any, navigate: any) => {
  console.log("comment data ", data);
  const response = await fetch(`${API_BASE}/api/comments/v1/add-comment`, {
    method: "POST",
    headers: getAuthHeaders(),
    body: JSON.stringify(data),
  });
  if (handleAuthRedirect(response, navigate)) return null;
  if (!response.ok) throw new Error("Failed to create comment");
  return response.json();
};

export const fetchAttachments = async (id: string, navigate: any) => {
  const response = await fetch(
    `${API_BASE}/api/attachments/v1/task?taskId=${id}`,
    {
      headers: getAuthHeaders(),
    }
  );
  if (handleAuthRedirect(response, navigate)) return [];
  if (!response.ok) throw new Error("Failed to fetch attachments");
  return response.json();
};

export const fetchComments = async (id: string, navigate: any) => {
  const response = await fetch(
    `${API_BASE}/api/comments/v1/task?taskId=${id}`,
    {
      headers: getAuthHeaders(),
    }
  );
  if (handleAuthRedirect(response, navigate)) return [];
  if (!response.ok) throw new Error("Failed to fetch comments");
  return response.json();
};

export const updateTask = async (data: any, navigate: any) => {
  console.log("dataa", data);
  const response = await fetch(`${API_BASE}/api/tasks/v1/update`, {
    method: "PUT",
    headers: getAuthHeaders(),
    body: JSON.stringify(data),
  });
  if (handleAuthRedirect(response, navigate)) return null;
  if (!response.ok) throw new Error("Failed to update task");
  return response.json();
};

export const getTeamMembers = async (id: string, navigate: any) => {
  const response = await fetch(
    `${API_BASE}/api/projects/v1/team-members?id=${id}`,
    {
      headers: getAuthHeaders(),
    }
  );
  if (handleAuthRedirect(response, navigate)) return null;
  if (!response.ok) throw new Error("Failed to get team members");
  return response.json();
};

export const createUser = async (data: any, navigate: any) => {
  const response = await fetch(`${API_BASE}/api/users/v1`, {
    method: "POST",
    headers: getAuthHeaders(),
    body: JSON.stringify(data),
  });
  if (handleAuthRedirect(response, navigate)) return null;
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || "Failed to create user");
  }
  return response.json();
};

export const updateProject = async (data: any, navigate: any) => {
  console.log("ppprprp", getAuthHeaders());
  const response = await fetch(`${API_BASE}/api/projects/v1`, {
    method: "PUT",
    headers: getAuthHeaders(),
    body: JSON.stringify(data),
  });
  if (handleAuthRedirect(response, navigate)) return null;
  if (!response.ok) throw new Error("Failed to update project");
  return response.json();
};

export const updateTaskState = async (
  data: { id: string; state: string; reason?: string },
  navigate: any
) => {
  const { id, state, reason } = data;
  const queryParams = new URLSearchParams();
  queryParams.append("id", id);
  queryParams.append("state", state);
  if (reason) queryParams.append("reason", reason);
  const response = await fetch(
    `${API_BASE}/api/tasks/v1/set-state?${queryParams.toString()}`,
    {
      method: "PUT",
      headers: getAuthHeaders(),
    }
  );
  if (handleAuthRedirect(response, navigate)) return null;
  if (!response.ok) throw new Error("Failed to update task state");
  return response.json();
};

export const createTodo = async (data: any, navigate: any) => {
  const response = await fetch(`${API_BASE}/api/todos/v1`, {
    method: "POST",
    headers: getAuthHeaders(),
    body: JSON.stringify(data),
  });
  if (handleAuthRedirect(response, navigate)) return null;
  if (!response.ok) throw new Error("Failed to create todo");
  return response.json();
};

export const fetchTodos = async (taskId: string, navigate: any) => {
  const response = await fetch(`${API_BASE}/api/todos/v1?taskId=${taskId}`, {
    headers: getAuthHeaders(),
  });
  if (handleAuthRedirect(response, navigate)) return [];
  if (!response.ok) throw new Error("Failed to fetch todos");
  return response.json();
};

export const updateTodoState = async (
  id: string,
  state: boolean,
  navigate: any
) => {
  const response = await fetch(
    `${API_BASE}/api/todos/v1/update-state?id=${id}&state=${state}`,
    {
      method: "PUT",
      headers: getAuthHeaders(),
    }
  );
  if (handleAuthRedirect(response, navigate)) return [];
  if (!response.ok) throw new Error("Failed to update todo state");
  return response.json();
};

export const fetchTasks = async (navigate: any) => {
  const response = await fetch(`${API_BASE}/api/tasks/v1`, {
    headers: getAuthHeaders(),
  });
  if (handleAuthRedirect(response, navigate)) return [];
  if (!response.ok) throw new Error("Failed to fetch tasks");
  return response.json();
};

export const deleteProject = async (id: string, navigate: any) => {
  const response = await fetch(`${API_BASE}/api/projects/v1?id=${id}`, {
    method: "DELETE",
    headers: getAuthHeaders(),
  });
  if (handleAuthRedirect(response, navigate)) return [];
  if (!response.ok) throw new Error("Failed to delete project");
};

export const deleteTask = async (id: string, navigate: any) => {
  const response = await fetch(`${API_BASE}/api/tasks/v1?id=${id}`, {
    method: "DELETE",
    headers: getAuthHeaders(),
  });
  if (handleAuthRedirect(response, navigate)) return [];
  if (!response.ok) throw new Error("Failed to delete task");
};

export const getUserById = async (id: string, navigate: any) => {
  const response = await fetch(`${API_BASE}/api/users/v1/${id}`, {
    method: "GET",
    headers: getAuthHeaders(),
  });
  if (handleAuthRedirect(response, navigate)) return [];
  if (!response.ok) throw new Error("Failed to get user");

  return response.json();
};

export const fetchTasksByUserId = async (id: string, navigate: any) => {
  const response = await fetch(`${API_BASE}/api/tasks/v1/user/${id}`, {
    headers: getAuthHeaders(),
  });
  if (handleAuthRedirect(response, navigate)) return [];
  if (!response.ok) throw new Error("Failed to fetch tasks");
  return response.json();
};

export const fetchProjects = async (navigate: any) => {
  const response = await fetch(`${API_BASE}/api/projects/v1`, {
    headers: getAuthHeaders(),
  });
  if (handleAuthRedirect(response, navigate)) return [];
  if (!response.ok) throw new Error("Failed to fetch projects");
  return response.json();
};

export const fetchProjectsByManager = async (userId: string, navigate: any) => {
  const response = await fetch(
    `${API_BASE}/api/projects/v1/by-manager?userId=${userId}`,
    {
      headers: getAuthHeaders(),
    }
  );
  if (handleAuthRedirect(response, navigate)) return [];
  if (!response.ok) throw new Error("Failed to fetch projects");
  return response.json();
};

export const deleteUser = async (userId: string, navigate: any) => {
  const response = await fetch(`${API_BASE}/api/users/v1?id=${userId}`, {
    method: "DELETE",
    headers: getAuthHeaders(),
  });
  if (handleAuthRedirect(response, navigate)) return [];
  if (!response.ok) throw new Error("Failed to delete user");
};

export const updateUserEmail = async (
  id: string,
  email: string,
  navigate: any
) => {
  const response = await fetch(
    `${API_BASE}/api/users/v1/update-email?id=${id}&email=${email}`,
    {
      method: "PUT",
      headers: getAuthHeaders(),
    }
  );
  if (handleAuthRedirect(response, navigate)) return [];
  if (!response.ok) throw new Error("Failed to update user email");
  return response.json();
};

export const updateUserName = async (
  id: string,
  name: string,
  navigate: any
) => {
  const response = await fetch(
    `${API_BASE}/api/users/v1/update-name?id=${id}&name=${name}`,
    {
      method: "PUT",
      headers: getAuthHeaders(),
    }
  );
  if (handleAuthRedirect(response, navigate)) return [];
  if (!response.ok) throw new Error("Failed to update user name");
  return response.json();
};

export const fetchProjectsByTeamMember = async (
  userId: string,
  navigate: any
) => {
  const response = await fetch(
    `${API_BASE}/api/projects/v1/by-team-member?userId=${userId}`,
    {
      headers: getAuthHeaders(),
    }
  );
  if (handleAuthRedirect(response, navigate)) return [];
  if (!response.ok) throw new Error("Failed to fetch projects");
  return response.json();
};

export const changePassword = async (data: any) => {
  const token = localStorage.getItem("token");
  console.log("Sending Authorization Header:", `Bearer ${token}`);

  const response = await fetch(`${API_BASE}/auth/change-password`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({
      oldPassword: data.oldPassword,
      newPassword: data.newPassword,
    }),
  });

  const text = await response.text();
  let result;

  try {
    result = JSON.parse(text);
  } catch (err) {
    result = { message: text };
  }

  if (!response.ok) {
    throw new Error(result.message || "Change password failed");
  }

  return result;
};
