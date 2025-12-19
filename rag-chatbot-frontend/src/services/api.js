
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

// Ask a question (text)
export const sendQuestion = async (question) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/api/ai/prompt`, {
            params: { message: question }
        });
        return response.data;
    } catch (error) {
        console.error('Error sending question:', error);
        return 'Error sending question';
    }
};

// Upload an image and get description

// Upload image (no question)
export const uploadImage = async (file) => {
    try {
        const formData = new FormData();
        formData.append('file', file);
        // If your backend expects only POST with file, no question
        const response = await axios.post(`${API_BASE_URL}/api/image/describe`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
        return response.data;
    } catch (error) {
        console.error('Error uploading image:', error);
        return 'Error uploading image';
    }
};

// Ask a question about the uploaded image (send both image and question)
export const askImageQuestion = async (file, question) => {
    try {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('question', question);
        const response = await axios.post(`${API_BASE_URL}/api/image/describe`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
        return response.data;
    } catch (error) {
        console.error('Error asking about image:', error);
        return 'Error asking about image';
    }
};

// Generate image from description
export const generateImage = async (prompt) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/api/image/generate/${encodeURIComponent(prompt)}`);
        return response.data;
    } catch (error) {
        console.error('Error generating image:', error);
        return 'Error generating image';
    }
};


// Upload audio file (no question)
export const uploadAudio = async (file) => {
    // Try both 'file' and 'audio' field names for compatibility
    let lastError = '';
    for (const fieldName of ['file', 'audio']) {
        try {
            const formData = new FormData();
            formData.append(fieldName, file);
            const response = await axios.get(`${API_BASE_URL}/api/audio/to-text`, formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });
            return response.data;
        } catch (error) {
            if (error.response) {
                lastError = `Error uploading audio with field '${fieldName}': ${error.response.data?.message || error.response.statusText}`;
                console.error(lastError, error.response.data);
            } else {
                lastError = `Error uploading audio with field '${fieldName}': ${error.message}`;
                console.error(lastError, error);
            }
        }
    }
    return lastError || 'Error uploading audio';
};

// Ask a question about the uploaded audio
export const askAudioQuestion = async (question) => {
    try {
        // If your backend expects a GET with ?question=...
        const response = await axios.get(`${API_BASE_URL}/api/audio/to-text`, {
            params: { question }
        });
        return response.data;
    } catch (error) {
        console.error('Error asking about audio:', error);
        return 'Error asking about audio';
    }
};

// Generate audio from text
export const generateAudio = async (text) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/api/audio/text-to-audio/${encodeURIComponent(text)}`, {
            responseType: 'blob'
        });
        return response.data;
    } catch (error) {
        console.error('Error generating audio:', error);
        return null;
    }
};

// Upload a web page URL
export const uploadUrl = async (url) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/api/rag/ingest-url`, { url });
        return response.data;
    } catch (error) {
        console.error('Error uploading URL:', error);
        return 'Error uploading URL';
    }
};

// Ask question based on URL data
export const askUrlData = async (question) => {
    try {
        // Backend expects { query: ... }
        const response = await axios.post(`${API_BASE_URL}/api/rag/ask-body-ulr`, { query: question });
        return response.data;
    } catch (error) {
        console.error('Error asking URL data:', error);
        return 'Error asking URL data';
    }
};

// Upload PDF
export const uploadPdf = async (file) => {
    try {
        const formData = new FormData();
        formData.append('file', file);
        const response = await axios.post(`${API_BASE_URL}/api/rag/ingest/pdf`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
        return response.data;
    } catch (error) {
        console.error('Error uploading PDF:', error);
        return 'Error uploading PDF';
    }
};

// Ask question based on PDF data
export const askPdfData = async (question) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/api/rag/ask`, {
            params: { question }
        });
        return response.data;
    } catch (error) {
        console.error('Error asking PDF data:', error);
        return 'Error asking PDF data';
    }
};