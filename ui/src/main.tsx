import { createRoot } from "react-dom/client";
import GamesPage from "./games/GamesPage";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import GamePage from "./game/GamePage";
import { gamePageLoader } from "./game/loader";
import '@fontsource/roboto/300.css';
import '@fontsource/roboto/400.css';
import '@fontsource/roboto/500.css';
import '@fontsource/roboto/700.css';
import { ThemeProvider } from "@emotion/react";
import { Container, CssBaseline } from "@mui/material";
import { createTheme } from '@mui/material/styles';
import { blue, red, teal } from '@mui/material/colors';

const theme = createTheme({
  palette: {
    primary: {
      main: blue[600],
    },
    secondary: {
      main: teal[600],
    },
    error: {
      main: red[600],
    },
  },
});

const router = createBrowserRouter([
  {
    path: "/",
    element: <GamesPage />
  },
  {
    path: "/games/:id/players/:player",
    element: <GamePage />,
    loader: gamePageLoader
  }
]);

createRoot(document.getElementById("root")!).render(
  <ThemeProvider theme={theme}>
    <CssBaseline />
    <Container maxWidth="xl" sx={{ display: "flex", flexDirection: "column", justifyContent: "center" }}>
      <RouterProvider router={router} />
    </Container>
  </ThemeProvider>
);
