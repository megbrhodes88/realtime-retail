import { createContext } from 'react';

export const CartContext = createContext({
    itemsInCart: 0,
    setItemsInCart: () => {},
});