const baseUrl = 'http://localhost:8080'; // Replace with your actual domain

const fileInput = document.getElementById('fileInput');
const fileNameDiv = document.getElementById('fileName');
const uploadBtn = document.getElementById('uploadBtn');
const downloadBtn = document.getElementById('downloadBtn');
const downloadLink = document.getElementById('downloadLink');

let uploadedFile; // To store the uploaded file

// Function to handle errors
function showError(message) {
  const errorDiv = document.createElement('div');
  errorDiv.classList.add('error-message');
  errorDiv.textContent = message;
  document.body.appendChild(errorDiv);

  setTimeout(() => {
    errorDiv.remove();
  }, 3000);
}

// Upload button click event
uploadBtn.addEventListener('click', async function() {
  const file = fileInput.files[0];
  if (file) {
    uploadedFile = file;
    fileNameDiv.textContent = `Selected file: ${file.name}`;
    try {
      const formData = new FormData();
      formData.append('file', file);

      const uploadResponse = await fetch(`${baseUrl}/upload`, {
        method: 'POST',
        body: formData,
      });

      if (uploadResponse.ok) {
        console.log('File uploaded successfully!');
      } else {
        showError('Upload failed. Please try again.');
        console.error('Upload failed');
      }
    } catch (error) {
      showError('Error uploading file. Please try again.');
      console.error('Error:', error);
    }
  } else {
    fileNameDiv.textContent = '';
    uploadedFile = undefined;
    showError('Please select a file to upload');
    console.error('Please select a file to upload');
  }
});

// Download button click event
downloadBtn.addEventListener('click', async function() {
  if (uploadedFile) {
    try {
      const downloadResponse = await fetch(`${baseUrl}/download`, {
        method: 'GET',
      });

      if (downloadResponse.ok) {
        const blob = await downloadResponse.blob();
        const fileURL = URL.createObjectURL(blob);

        // Set the download link href and trigger the download
        downloadLink.href = fileURL;
        downloadLink.click();
      } else {
        showError('Download failed. Please try again.');
        console.error('Download failed');
      }
    } catch (error) {
      showError('Error downloading file. Please try again.');
      console.error('Error:', error);
    }
  } else {
    showError('Please upload a file first');
    console.error('Please upload a file first');
  }
});
