export type ToSelectOption<T, ValueKey extends keyof any = keyof T, LabelKey extends keyof any = keyof T> = Array<{
    // Only index T with keys that actually exist on T
    label: T[Extract<LabelKey, keyof T>];
    value: T[Extract<ValueKey, keyof T>];
}>