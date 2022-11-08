import React from 'react';

export const InventoryContext = React.createContext({
    inventory: 0,
    syncInventory: () => {}
});