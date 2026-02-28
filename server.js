const express = require("express");
const path = require("path");

const app = express();

// IMPORTANT: Render provides dynamic PORT
const PORT = process.env.PORT || 3000;

app.use(express.static(path.join(__dirname, "public")));

app.get("/", (req, res) => {
    res.sendFile(path.join(__dirname, "public", "index.html"));
});

app.listen(PORT, () => {
    console.log(`Lunara running on port ${PORT}`);
});