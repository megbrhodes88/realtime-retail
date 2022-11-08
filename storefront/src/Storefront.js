import { BrowserRouter, Routes, Route } from "react-router-dom";
import { Box } from '@mui/material';
import Navbar from "./components/Navbar";
import Products from "./pages/Products";
import { useEffect, useState } from 'react';
import Product from "./pages/Product";
import Sales from "./pages/Sales";
import Cart from './pages/Cart';
import Simulate from './pages/Simulate';
import { CartContext } from "./contexts/CartContext";
import { StompSessionProvider } from 'react-stomp-hooks';

export default function Storefront() {
    // I am quite broken to how to get the below from an environment variable. It's set on the host, but always
    // returns undefined bellow. I believe it's because this is running in the browser, where it's not set. 
    const APISERVER_HOSTNAME = "a2d186108d0264bd7a8bec10e384d8c3-635988121.us-east-1.elb.amazonaws.com";
    const [itemsInCart, setItemsInCart] = useState(0);
    const wsSourceUrl = `http://${APISERVER_HOSTNAME}:8080/handler`;

    useEffect(() => {
        if (sessionStorage.getItem("userShoppingCart") === null) {
            sessionStorage.setItem("userShoppingCart", JSON.stringify([]));
        } else if (JSON.parse(sessionStorage.getItem("userShoppingCart")) === []) {
            setItemsInCart(0)
        } else {
            const currentCart = JSON.parse(sessionStorage.getItem("userShoppingCart"));
            setItemsInCart(currentCart.length)
        }
    }, [])

    const addOrRemoveItemsInCart = (updatedItemsInCart) => {
        setItemsInCart(updatedItemsInCart);
    }

    return (
        // TODO Right now, there's a bunch of leftovers for the "reserved inventory", this won't be 
        // feasible until something like Shared Web Workers is implemented. 
        <Box sx={{ display: "grid", gridTemplateColumns: "100vw", gridTemplateRows: "100vh", justifyItems: "stretch" }}> 
            <StompSessionProvider url={ wsSourceUrl }>
            <CartContext.Provider value={{ itemsInCart, addOrRemoveItemsInCart }}>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={ <Navbar/> }>
                        <Route index element={ <Products/> }/>
                        <Route path="products/:productId" element={ <Product/> }/>
                        <Route path="sales" element={ <Sales/> }/>
                        <Route path="cart" element={ <Cart/> }/>
                        <Route path="simulate" element={ <Simulate/> }/>
                    </Route>
                </Routes>
            </BrowserRouter>
            </CartContext.Provider>
            </StompSessionProvider>
        </Box>
    );
};