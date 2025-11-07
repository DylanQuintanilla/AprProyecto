// src/hooks/useToast.js

export const useToast = () => {
    const Toastify = window.Toastify;

    const showToast = (message, type = 'success') => {
        if (!Toastify) {
            console.error("Toastify no cargado. Revisa la CDN en index.html");
            return;
        }

        let backgroundColor = '';
        switch (type) {
            case 'success':
                backgroundColor = "linear-gradient(to right, #00b09b, #96c93d)";
                break;
            case 'error':
                backgroundColor = "linear-gradient(to right, #ff5f6d, #ffc371)";
                break;
            case 'info':
                backgroundColor = "linear-gradient(to right, #4facfe, #00f2fe)";
                break;
            default:
                backgroundColor = "linear-gradient(to right, #00b09b, #96c93d)";
        }

        Toastify({
            text: message,
            duration: 3000,
            close: true,
            gravity: "top", 
            position: "right", 
            style: {
                background: backgroundColor,
            }
        }).showToast();
    };

    return { showToast };
};