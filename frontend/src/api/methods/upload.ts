import {instance} from "/@/api";

export const uploadFile = (
    files: (File | Blob)[] = [],
    params: { url: string; request?: Record<string, any> | string; fileFieldName?: string } = { url: '' },
    isMultiple = false,
) => {
    const formData = new FormData();
    const fileName = params.fileFieldName || (isMultiple ? 'file' : 'file');

    (Array.isArray(files) ? files : [files]).forEach((file) => {
        if (file) {
            formData.append(fileName, file);
        }
    });

    if (params.request !== undefined && params.request !== null) {
        const requestData = typeof params.request === 'string' ? params.request : JSON.stringify(params.request);
        formData.append('request', new Blob([requestData], { type: 'application/json;charset=UTF-8' }));
    }

    return instance.Post<string>(params.url, formData);
}