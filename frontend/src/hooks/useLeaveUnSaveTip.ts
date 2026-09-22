export interface LeaveProps {
    leaveTitle: string;
    leaveContent: string;
    tipType: 'error' | 'success' | 'warning'
}

const leaveProps: LeaveProps = {
    leaveTitle: '离开此页面？',
    leaveContent: '系统不会保存您所做的更改',
    tipType: 'warning'
};
export const useLeaveUnSaveTip = (leaveProp = leaveProps) => {
    const {leaveTitle, leaveContent, tipType} = leaveProp;
    const isSave = ref(true);

    const setIsSave = (flag: boolean) => {
        isSave.value = flag;
    };
    const openUnsavedTip = (next: () => void) => {
        window.$dialog.create({
            type: tipType,
            title: leaveTitle,
            content: leaveContent,
            negativeText: '留下',
            positiveText: '离开',
            onPositiveClick: () => {
                isSave.value = true;
                next();
            }
        })
    }
    onBeforeRouteLeave((to, from, next) => {
        if (to.path === from.path) {
            next();
            return;
        }

        if (!isSave.value) {
            openUnsavedTip(next);
        } else {
            next();
            // return;
        }
    });

    // 页面有变更时，关闭或刷新页面弹出浏览器的保存提示
    window.onbeforeunload = () => {
        if (!isSave.value) {
            return '';
        }
    };
    return {
        setIsSave,
        openUnsavedTip,
        isSave,
    };
}