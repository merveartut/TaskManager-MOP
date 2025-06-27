import { CircleAlert, LogOut, Menu } from "lucide-react";
import React, { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { TooltipHint } from "../Tooltip/TooltipHint";
import { Avatar, Drawer, IconButton } from "@mui/material";
import { Modal } from "../Modal/Modal";
import mopLogo from "../../assets/mop.png";

interface NavbarProps {
  items: any[];
  userInfo?: any;
}

export const Navbar: React.FC<NavbarProps> = ({ items, userInfo }) => {
  const location = useLocation();
  const [expanded, setExpanded] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);
  const navigate = useNavigate();
  const [isMobile, setIsMobile] = useState(false);

  // Detect mobile viewport width < 640px
  useEffect(() => {
    const onResize = () => setIsMobile(window.innerWidth < 640);
    onResize();
    window.addEventListener("resize", onResize);
    return () => window.removeEventListener("resize", onResize);
  }, []);

  // When mobile and collapsed, only render floating menu button

  // Helper to get user initials
  const getInitials = (name: string) => {
    if (!name) return null;
    const parts = name.trim().split(" ");
    const first = parts[0]?.[0] || "";
    const second = parts[1]?.[0] || "";
    return (first + second).toLowerCase();
  };

  // Sidebar content (used inside Drawer or desktop sidebar)
  // The sidebarContent here expects expanded boolean to decide if names are shown.
  const sidebarContent = (showText: boolean) => (
    <div
      className={`min-h-screen bg-gray-900 text-white flex flex-col p-4 ${
        showText ? "w-60" : "w-16"
      }`}
    >
      <div
        className={`flex items-center mb-6 ${
          showText ? "justify-between" : "justify-center"
        } w-full`}
      >
        {showText ? (
          <>
            <div
              className="flex items-center gap-2 cursor-pointer"
              onClick={() => {
                navigate("/projects");
                if (isMobile) setMobileOpen(false);
              }}
            >
              <img src={mopLogo} width={32} />
              <h2 className="text-xl font-bold font-roboto">MOP</h2>
            </div>
          </>
        ) : (
          <img
            src={mopLogo}
            width={32}
            className="cursor-pointer"
            onClick={() => {
              navigate("/projects");
              if (isMobile) setMobileOpen(false);
            }}
          />
        )}
      </div>

      <nav className="flex flex-col space-y-4 flex-1">
        {items.map(
          (item) =>
            item.display && (
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center p-2 rounded-lg transition ${
                  location.pathname === item.path
                    ? "bg-blue-600"
                    : "hover:bg-gray-700"
                }`}
                onClick={() => isMobile && setMobileOpen(false)}
              >
                <div>{item.icon}</div>
                {showText && <span className="ml-3">{item.name}</span>}
              </Link>
            )
        )}
      </nav>

      {userInfo && (
        <div className="flex flex-col gap-4 w-full mt-auto">
          {userInfo.userRole !== "GUEST" && (
            <TooltipHint text={userInfo.userName}>
              <Link
                to={userInfo.path}
                className={`flex items-center py-2 rounded-lg transition ${
                  location.pathname === userInfo.path
                    ? "bg-blue-600"
                    : "hover:bg-gray-700"
                }`}
                onClick={() => isMobile && setMobileOpen(false)}
              >
                <Avatar alt={userInfo.userName}>
                  {getInitials(userInfo.userName)}
                </Avatar>
                {showText && <span className="ml-3">{userInfo.name}</span>}
              </Link>
            </TooltipHint>
          )}
          <TooltipHint text="Logout">
            <button
              className="flex items-center p-2 rounded-lg transition"
              onClick={() => {
                setIsModalOpen(true);
                setMobileOpen(false);
              }}
            >
              <LogOut />
              {showText && <span className="ml-3">Logout</span>}
            </button>
          </TooltipHint>
        </div>
      )}
    </div>
  );

  // Mobile: when screen <640px, render a floating button or drawer
  if (isMobile) {
    return (
      <>
        {/* Fixed top navbar for mobile */}
        <div className="fixed top-0 left-0 right-0 z-50 bg-gray-900 text-white flex items-center justify-between px-4 py-3 shadow-md">
          <div
            className="flex items-center gap-2 cursor-pointer"
            onClick={() => navigate("/projects")}
          >
            <img src={mopLogo} width={28} />
            <h2 className="text-lg font-bold font-roboto">MOP</h2>
          </div>
          <IconButton
            onClick={() => setMobileOpen(true)}
            aria-label="Open menu"
            className="!text-white"
          >
            <Menu />
          </IconButton>
        </div>

        {/* Drawer slides from left */}
        <Drawer
          anchor="right"
          open={mobileOpen}
          onClose={() => setMobileOpen(false)}
          ModalProps={{ keepMounted: true }}
        >
          {sidebarContent(true)}
        </Drawer>

        {/* Logout Confirmation Modal */}
        <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)}>
          <div className="flex flex-row items-center gap-2 justify-center">
            <CircleAlert size={32} color="blue" />
            <span className="text-black">
              Are you sure you want to log out?
            </span>
          </div>

          <div className="flex flex-row justify-end gap-6">
            <button
              type="button"
              className="mt-4 bg-slate-500 text-white py-2 px-4 rounded"
              onClick={() => setIsModalOpen(false)}
            >
              No
            </button>
            <button
              type="button"
              className="mt-4 bg-blue-700 text-white py-2 px-4 rounded"
              onClick={() => {
                setIsModalOpen(false);
                navigate("/");
              }}
            >
              Yes
            </button>
          </div>
        </Modal>
      </>
    );
  }

  // Desktop sidebar with hover expand/collapse
  return (
    <>
      <div
        className={`min-h-screen bg-gray-900 text-white flex flex-col items-center p-4 transition-all duration-300 ${
          expanded ? "w-60" : "w-16"
        }`}
        onMouseEnter={() => setExpanded(true)}
        onMouseLeave={() => setExpanded(false)}
      >
        {sidebarContent(expanded)}
      </div>

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)}>
        <div className="flex flex-row items-center gap-2 justify-center">
          <CircleAlert size={32} color="blue" />
          <span className="text-black">Are you sure you want to log out ?</span>
        </div>

        <div className="flex flex-row justify-end gap-6">
          <button
            type="submit"
            className="mt-4 bg-slate-500 text-white py-2 px-4 rounded"
            onClick={() => setIsModalOpen(false)}
          >
            No
          </button>
          <button
            type="submit"
            className="mt-4 bg-blue-700 text-white py-2 px-4 rounded"
            onClick={() => {
              setIsModalOpen(false);
              navigate("/");
            }}
          >
            Yes
          </button>
        </div>
      </Modal>
    </>
  );
};
