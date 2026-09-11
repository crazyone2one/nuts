export interface PageQuery<T = Record<string, any>> {
    page: number;
    pageSize: number;
    filter?: Partial<T>
}

export interface PageResult<T> {
    [x: string]: any;

    pageSize: number;
    totalPage: number;
    pageNumber: number;
    totalRow: number;
    records: T[];
}

export interface ApiResponse<T> {
    code: number;
    message: string;
    messageDetail: string;
    data: T;
}