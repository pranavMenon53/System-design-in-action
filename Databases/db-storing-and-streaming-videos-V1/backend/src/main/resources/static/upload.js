document.getElementById("uploadBtn").addEventListener("click", async (e) => {
  e.preventDefault();

  const fileInput = document.getElementById("fileInput");
  const file = fileInput.files[0];
  if (!file) return;

  const chunkSize = 5 * 1024 * 1024; // 5 MB
  const totalChunks = Math.ceil(file.size / chunkSize);

  for (let i = 0; i < totalChunks; i++) {
    const start = i * chunkSize;
    const end = Math.min(start + chunkSize, file.size);
    const chunk = file.slice(start, end);

    const formData = new FormData();
    formData.append("chunk", chunk);
    formData.append("chunkIndex", i);
    formData.append("totalChunks", totalChunks);
    formData.append("fileName", file.name);

    try {
      let response = await fetch("http://localhost:8080/upload", {
        method: "POST",
        body: formData,
      });

      if (!response.ok) {
        alert("Upload failed at chunk " + i);
        return;
      }
    } catch (error) {
      alert("Network error: " + error);
      console.log("error", error);
      return;
    }

    const progress = ((i + 1) / totalChunks) * 100;
    document.getElementById("progressBar").value = progress;
  }

  alert("Upload complete!");
});
