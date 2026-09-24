import {instance} from "/@/api";
import type {FileListQueryParams, FilePageRes, ModuleCount} from "/@/types/file.ts";
import {uploadFile} from "/@/api/methods/upload.ts";

export const fileManagementApi = {
    getModulesCount: (query: FileListQueryParams) => instance.Post<ModuleCount>('/project/file/module/count', query),
    getFileList: (query: FileListQueryParams) => instance.Post<FilePageRes>('/project/file/page', query),
    getFileTypes: (id: string) => instance.Get<Array<string>>('/project/file/type', {params: id}),
    uploadFile: (data: { request: any; fileList?: (File | Blob)[] }) => uploadFile(data.fileList ?? [],
        {
            url: '/project/file/upload',
            request: data.request,
            fileFieldName: 'file',
        }, false)
}