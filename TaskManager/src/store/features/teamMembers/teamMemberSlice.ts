import { createAsyncThunk, createSlice, PayloadAction } from "@reduxjs/toolkit";
import { getTeamMembers } from "../../../services/projectApi";


interface User {
    id: string;
    name: string;
}

interface State {
    teamMembers: User[];
    loading: boolean;
    error: string | null;
}

const initialState: State = {
    teamMembers: [],
    loading: false,
    error: null,
  };

  export const fetchTeamMembersThunk = createAsyncThunk(
    "teamMembers/fetchAll",
    async (projectId: string, { rejectWithValue }) => {
      console.log("🚀 Thunk called with projectId:", projectId);
      try {
        const navigate = () => {};
        const response = await getTeamMembers(projectId, navigate);
        console.log("✅ Team members fetched:", response);
        return response;
      } catch (err) {
        console.error("❌ Error in thunk:", err);
        return rejectWithValue("Failed to fetch team members");
      }
    }
  );

const teamMembersSlice = createSlice({
    name: "teamMembers",
    initialState,
    reducers: {
        setTeamMembersStore(state, action: PayloadAction<User[]>) {
            state.teamMembers = action.payload
        },
        clearTeamMembers(state) {
            state.teamMembers = []
        }
    },
    extraReducers: (builder) => {
        builder
        .addCase(fetchTeamMembersThunk.pending, (state) => {
            state.loading = true;
            state.error = null;
        })
        .addCase(fetchTeamMembersThunk.fulfilled, (state, action: PayloadAction<User[]>) => {
            state.loading = false;
            state.teamMembers = action.payload;
        })
        .addCase(fetchTeamMembersThunk.rejected, (state, action) => {
            state.loading = false;
            state.error = action.payload as string;
        });
    }
})

export const { setTeamMembersStore, clearTeamMembers } = teamMembersSlice.actions

export default teamMembersSlice.reducer;