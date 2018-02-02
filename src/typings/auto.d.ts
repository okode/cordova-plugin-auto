declare module Auto {
    interface AutoPlugin {
        sendMessage(conversationId: number, from: string, message: string, success: () => any, error: () => any): void;
        isCarUIMode(success: () => any, error: () => any): void;
        register(success: (event: any) => any, error: () => any): void;
    }
}

declare var Auto: Auto.AutoPlugin;
