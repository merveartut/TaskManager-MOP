import "./App.css";
import { LoginPage } from "./pages/LoginPage/LoginPage";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
  useLocation,
} from "react-router-dom";
import { Projects } from "./pages/DashboardPage/Projects";
import { Navbar } from "./components/Navbar/Navbar";
import { Folder, List, Settings } from "lucide-react";
import { ProjectDetailPage } from "./pages/ProjectDetailPage/ProjectDetailPage";
import { TaskDetailPage } from "./pages/TaskDetailPage/TaskDetailPage";
import { SettingsPage } from "./pages/SettingsPage/SettingsPage";
import { Tasks } from "./pages/DashboardPage/Tasks";
import { Toaster } from "react-hot-toast";
import { ProfilePage } from "./pages/ProfilePage/ProfilePage";
import { JSX, useEffect, useState } from "react";

function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}

function AppContent() {
  const location = useLocation();
  const hideNavbarRoutes = ["/login"];

  const userName = localStorage.getItem("userName");
  const userId = localStorage.getItem("userId");
  const currentUserRole = localStorage.getItem("userRole");

  const menuItems = [
    {
      name: "Projects",
      path: "/projects",
      icon: <Folder size={20} />,
      display: true,
    },
    { name: "Tasks", path: "/tasks", icon: <List size={20} />, display: true },
    {
      name: "User Settings",
      path: "/settings",
      icon: <Settings size={20} />,
      display: currentUserRole === "ADMIN" || currentUserRole === "GUEST",
    },
  ];

  const userInfo = {
    path: `/profile/${userId}`,
    userName: userName,
    userId: userId,
    name: "Profile",
    userRole: currentUserRole,
  };

  function PrivateRoute({ children }: { children: JSX.Element }) {
    const [isAuthChecked, setIsAuthChecked] = useState(false);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
      const token = localStorage.getItem("token");
      const role = localStorage.getItem("userRole");

      console.log(token);
      console.log(role);

      if (token || role === "GUEST") {
        setIsAuthenticated(true);
      }
      setIsAuthChecked(true);
    }, []);

    if (!isAuthChecked) {
      return <div>Loading...</div>; // or a spinner
    }

    return isAuthenticated ? children : <Navigate to="/login" />;
  }

  return (
    <div className="flex h-screen">
      {!hideNavbarRoutes.includes(location.pathname) && (
        <Navbar items={menuItems} userInfo={userInfo} />
      )}
      <Toaster
        position="top-right"
        reverseOrder={false}
        toastOptions={{
          style: {
            width: "400px",
            height: "80px",
          },
        }}
      />
      <div className="flex-1 overflow-y-auto md:pt-0 lg:pt-0 pt-16">
        <Routes>
          <Route path="/login" element={<LoginPage />} />

          <Route
            path="/projects"
            element={
              <PrivateRoute>
                <Projects />
              </PrivateRoute>
            }
          />
          <Route
            path="/profile/:id"
            element={
              <PrivateRoute>
                <ProfilePage />
              </PrivateRoute>
            }
          />
          <Route
            path="/projectDetail/:id"
            element={
              <PrivateRoute>
                <ProjectDetailPage />
              </PrivateRoute>
            }
          />
          <Route
            path="/taskDetail/:id"
            element={
              <PrivateRoute>
                <TaskDetailPage />
              </PrivateRoute>
            }
          />
          <Route
            path="/settings"
            element={
              <PrivateRoute>
                <SettingsPage />
              </PrivateRoute>
            }
          />
          <Route
            path="/tasks"
            element={
              <PrivateRoute>
                <Tasks />
              </PrivateRoute>
            }
          />

          <Route path="*" element={<Navigate to="/login" />} />
        </Routes>
      </div>
    </div>
  );
}

export default App;
