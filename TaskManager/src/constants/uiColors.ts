export const stateColors: { [key: string]: string } = {
    BACKLOG: "bg-gray-200 text-gray-800",
    IN_ANALYSIS: "bg-yellow-100 text-yellow-800",
    IN_DEVELOPMENT: "bg-blue-100 text-blue-800",
    BLOCKED: "bg-red-200 text-red-800",
    CANCELLED: "bg-gray-300 text-gray-600 line-through",
    COMPLETED: "bg-green-100 text-green-800",
  };

export const projectStatusColors: { [key: string]: string } = {
  IN_PROGRESS: "bg-purple-100 text-purple 800",
  CANCELLED: "bg-gray-300 text-gray-600 line-through",
  COMPLETED:"bg-green-100 text-green-800"
}

export const taskPriorityColors: { [key: string]: string } = {
  CRITICAL: "bg-red-100 text-red 800",
  HIGH: "bg-purple-100 text-purple 800",
  MEDIUM:"bg-blue-100 text-blue 800",
  LOW: "bg-yellow-100 text-yellow 800"
}