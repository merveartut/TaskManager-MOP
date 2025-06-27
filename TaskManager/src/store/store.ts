import { configureStore } from "@reduxjs/toolkit";
import teamMembersReducer from "./features/teamMembers/teamMemberSlice"

export const store = configureStore({
    reducer: {
        teamMembers: teamMembersReducer
    }
})

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;