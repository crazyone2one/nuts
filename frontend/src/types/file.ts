import type {PageQuery, PageResult} from "/@/types/common.ts";

export interface ModuleCount {
    [key: string]: number;
}
export interface FileListQueryParams extends Partial<PageQuery> {
    moduleIds: string[];
    fileType: string;
    projectId: string;
}
export type FileStorageType = 'GIT' | 'MINIO';
// 文件列表项
export interface FileItem {
    id: string;
    name: string;
    originalName: string; // 原文件名
    fileType: string; // 文件类型
    tags: string[]; // 标签
    description: string;
    updateUser: string;
    updateTime: number;
    previewSrc: string; // 预览地址
    size: number;
    enable: boolean; // jar文件启用禁用
    branch?: string; // 分支
    filePath?: string; // 文件路径
    fileVersion?: string; // 文件版本
    storage?: FileStorageType; // 存储方式
}
export type FilePageRes = PageResult<FileItem>