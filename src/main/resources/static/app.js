const uploadForm = document.querySelector("#uploadForm");
const javaZipFile = document.querySelector("#javaZipFile");
const selectedFileName = document.querySelector("#selectedFileName");
const keepCommentsInHash = document.querySelector("#keepCommentsInHash");
const organiseButton = document.querySelector("#organiseButton");
const statusMessage = document.querySelector("#statusMessage");
const resultPanel = document.querySelector("#resultPanel");
const scannedCount = document.querySelector("#scannedCount");
const keptCount = document.querySelector("#keptCount");
const removedCount = document.querySelector("#removedCount");
const dropZone = document.querySelector(".drop-zone");
const progressTrack = document.querySelector("#progressTrack");

javaZipFile.addEventListener("change", () => {
    updateSelectedFile(javaZipFile.files[0]);
});

uploadForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const selectedFile = javaZipFile.files[0];
    if (!selectedFile) {
        showStatus("Choose a ZIP file first.", "error");
        return;
    }

    const formData = new FormData();
    formData.append("file", selectedFile);
    formData.append("keepCommentsInHash", keepCommentsInHash.checked);

    setBusy(true);
    showStatus("Organising your Java codes...", "neutral");

    try {
        const response = await fetch("/api/files/organise", {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            const error = await response.json().catch(() => ({ message: "Upload failed." }));
            throw new Error(error.message || "Upload failed.");
        }

        const zipBlob = await response.blob();
        downloadBlob(zipBlob, "organised-java-codes.zip");

        scannedCount.textContent = response.headers.get("X-Java-Files-Scanned") || "0";
        keptCount.textContent = response.headers.get("X-Unique-Files-Kept") || "0";
        removedCount.textContent = response.headers.get("X-Duplicate-Files-Removed") || "0";
        resultPanel.hidden = false;

        showStatus("Done. Your organised ZIP is downloading.", "success");
    } catch (error) {
        showStatus(error.message, "error");
    } finally {
        setBusy(false);
    }
});

function setBusy(isBusy) {
    organiseButton.disabled = isBusy;
    organiseButton.textContent = isBusy ? "Organising..." : "Organise and Download";
    progressTrack.classList.toggle("is-active", isBusy);
}

function showStatus(message, type) {
    statusMessage.textContent = message;
    statusMessage.classList.remove("is-error", "is-success");

    if (type === "error") {
        statusMessage.classList.add("is-error");
    }

    if (type === "success") {
        statusMessage.classList.add("is-success");
    }
}

function downloadBlob(blob, filename) {
    const downloadUrl = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = downloadUrl;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    link.remove();
    URL.revokeObjectURL(downloadUrl);
}

dropZone.addEventListener("dragover", (event) => {
    event.preventDefault();
    dropZone.classList.add("is-dragging");
});

dropZone.addEventListener("dragleave", () => {
    dropZone.classList.remove("is-dragging");
});

dropZone.addEventListener("drop", (event) => {
    event.preventDefault();
    dropZone.classList.remove("is-dragging");

    const droppedFile = event.dataTransfer.files[0];
    if (!droppedFile) {
        return;
    }

    const dataTransfer = new DataTransfer();
    dataTransfer.items.add(droppedFile);
    javaZipFile.files = dataTransfer.files;
    updateSelectedFile(droppedFile);
});

function updateSelectedFile(file) {
    if (!file) {
        selectedFileName.textContent = "Choose a .zip file";
        return;
    }

    const sizeInMb = file.size / (1024 * 1024);
    selectedFileName.textContent = `${file.name} (${sizeInMb.toFixed(2)} MB)`;
}
