import React, { useState } from "react";
import { Form } from "../../components/Form/Form";
import image from "../../assets/task_image.jpg";
import { useNavigate } from "react-router-dom";
import { Heart } from "lucide-react";
import mopLogo from "../../assets/mop_dark.png";

const loginFields = [
  { name: "username", label: "Username", type: "username", visible: true },
  { name: "password", label: "Password", type: "password", visible: true },
];
const API_BASE = "https://taskmanagerbackend-cgl2.onrender.com";

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setloading] = useState(false);
  const handleSubmit = async (formData: any) => {
    try {
      setloading(true);
      const response = await fetch(`${API_BASE}/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username: formData.username,
          password: formData.password,
        }),
      });
      if (!response.ok) {
        throw new Error("Invalid credentials");
      }

      const data = await response.json();
      console.log("Login successful", data);
      setloading(false);
      // Save the token
      localStorage.setItem("token", data.token);
      localStorage.setItem("userId", data.userId);
      localStorage.setItem("userRole", data.role);
      localStorage.setItem("userName", formData.username);
      navigate("/projects");
    } catch (error: any) {
      console.error("Login failed", error);
      alert("Login failed: " + error.message);
      setloading(false);
    }
  };

  const handleExplore = async () => {
    try {
      setloading(true);
      const res = await fetch(`${API_BASE}/auth/guest-token`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
      });
      const data = await res.json();
      setloading(false);
      localStorage.setItem("token", data.token);
      localStorage.setItem("userId", data.userId);
      localStorage.setItem("userRole", data.role);
      localStorage.setItem("userName", "guest");
      localStorage.setItem("guest", "true");
      navigate("/projects");
    } catch (err) {
      console.error("Failed to get guest token", err);
      alert("Unable to enter guest mode");
      setloading(false);
    }
  };
  return (
    <div className="flex flex-col md:flex-row w-full min-h-screen">
      <div className="max-h-screen hidden md:w-1/2 md:block">
        <img src={image} className="object-cover w-full h-screen"></img>
      </div>

      <div className="md:w-1/2 w-full flex flex-col justify-between min-h-screen py-8 px-4">
        {/* Centered Form Section */}
        <div className="flex flex-col items-center justify-center flex-grow">
          <div className="flex flex-col items-center w-full max-w-sm gap-8">
            <div className="flex items-center ml-4">
              <span className="text-3xl font-semibold font-roboto">MOP</span>
              <img src={mopLogo} width={32} />
            </div>
            <div className="w-full text-center">
              <h2 className="text-2xl font-bold">Login</h2>
              <p className="text-lg italic text-gray-600">MOP up tasks!</p>
            </div>

            <>
              <Form
                fields={loginFields}
                onSubmit={handleSubmit}
                loading={loading}
              />
              <button
                onClick={handleExplore}
                className="text-purple-800 hover:text-purple-600 text-sm font-medium mt-4"
                disabled={loading}
              >
                {loading ? (
                  <span className="inline-block animate-spin rounded-full h-5 w-5 border-t-2 border-white border-solid"></span>
                ) : (
                  "I just want to explore the app"
                )}
              </button>
            </>
          </div>
        </div>

        {/* Footer at Bottom */}
        <div className="flex justify-center items-center gap-2 text-gray-500 text-sm mt-8">
          <span className="font-roboto">Made by Merve Artut</span>
          <Heart size={14} />
        </div>
      </div>
    </div>
  );
};
