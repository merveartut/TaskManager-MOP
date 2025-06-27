// tailwind.config.js
module.exports = {
  content: [
    "./src/**/*.{js,ts,jsx,tsx}", // Ensure this path includes your JSX/TSX files
  ],
  theme: {
    extend: {
      fontFamily: {
        inter: ["Roboto", "sans-serif"],
        roboto: ["Roboto"], // for local font
      },
    },
  },
  plugins: [],
};
