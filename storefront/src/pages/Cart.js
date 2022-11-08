import React, { useLayoutEffect, useState, useContext, useEffect } from 'react';
import { Box, Typography, Card, CardMedia, Button, List, IconButton, CircularProgress, ListItem } from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';
import { AddCircle, RemoveCircle } from '@mui/icons-material';
import { CartContext } from '../contexts/CartContext';
import axios from 'axios';
import { v4 as uuid } from 'uuid';

// const productId = "5b82c9ep2j4b"
// const products = [
//     { productId: "2r96u3vx4i6k", carYear: "1990", carMake: "Lamborghini", carModel:"Countach", productSubtitle: "Networked well-modulated installation", productDesc: "n/a" },
//     { productId: "5k70t1yx5v9s", carYear: "1999", carMake: "Lamborghini", carModel:"Diablo", productSubtitle: "Multi-channelled hybrid support", productDesc: "n/a" }
// ]

export default function Cart() {
    const APISERVER_HOSTNAME = "a2d186108d0264bd7a8bec10e384d8c3-635988121.us-east-1.elb.amazonaws.com";
    const { addOrRemoveItemsInCart } = useContext(CartContext);
    const [cart, updateCart] = useState([]);
    const [selectedProductId, setSelectedProductId] = useState("");
    const [isCheckoutButtonDisabled, setIsCheckoutButtonDisabled] = useState(true);
    const [isOrderInFlight, setIsOrderInFlight] = useState(false);

    useLayoutEffect(() => {
        if (sessionStorage.getItem("userShoppingCart") === null) {
            sessionStorage.setItem("userShoppingCart", JSON.stringify([]));
            setSelectedProductId("dogesuchempty");
        } else if (sessionStorage.getItem("userShoppingCart") === "[]") {
            setSelectedProductId("dogesuchempty");
        } else {
            const currentCart = JSON.parse(sessionStorage.getItem("userShoppingCart"));
            updateCart(currentCart);
            setSelectedProductId(currentCart[0].productId);
        }
    }, [])

    useEffect(() => {
        if (cart.length === 0) { setIsCheckoutButtonDisabled(true) }
        else { setIsCheckoutButtonDisabled(false) }
    }, [cart])

    const removeProductFromCart = (product) => {
        const currentCart = JSON.parse(sessionStorage.getItem("userShoppingCart"));
        let indexToRemove = -1;
        for (let i=0; i < currentCart.length; i++) {
            if (currentCart[i].productId === product.productId) {
                indexToRemove = i;
            }
        }
        if (indexToRemove > -1) {
            currentCart.splice(indexToRemove, 1);
            sessionStorage.setItem("userShoppingCart", JSON.stringify(currentCart));
            updateCart([...currentCart]);
            addOrRemoveItemsInCart(currentCart.length);
            if (currentCart.length > 0) {
                setSelectedProductId(String(currentCart[0].productId))
            } else {
                setSelectedProductId("dogesuchempty")
            }
        } else {
            console.log("item not in cart somehow");
        }
    }

    const handleCreateOrder = () => {
        // could add some kind of validation that there is anything in the cart at all
        let newOrderItems = [];
        cart.map((product) => { newOrderItems.push({ productId: product.productId, quantityPurchased: product.quantity })});
        setIsOrderInFlight(true);
        setIsCheckoutButtonDisabled(true);
        setTimeout(() => { 
            setIsOrderInFlight(false); 
            setIsCheckoutButtonDisabled(false); 
            // setSnackbarOpen(true); 
        }, 1000);
        axios.post(`http://${APISERVER_HOSTNAME}:8080/api/orders`, {
            orderId: uuid(),
            orderItems: newOrderItems
        }, { "Content-Type" : "Application/json"}).then((res) => {

        }).catch((err) => {
            console.log(err);
        })
        // need to redirect to order confirmation page and/or cleanup cart
        // or not since this isn't a real app. much nicer to be able to create many orders
    }

    const increaseProductQuantity = (product) => {
        let currentCart = JSON.parse(sessionStorage.getItem("userShoppingCart"));
        const productIndex = currentCart.findIndex((productInCart) => { return productInCart.productId === product.productId });
        currentCart[productIndex].quantity += 1;
        const updatedCart = currentCart;
        sessionStorage.setItem("userShoppingCart", JSON.stringify(updatedCart));
        updateCart(updatedCart);
    }
    const decreaseProductQuantity = (product) => {
        let currentCart = JSON.parse(sessionStorage.getItem("userShoppingCart"));
        const productIndex = currentCart.findIndex((productInCart) => { return productInCart.productId === product.productId });
        if (currentCart[productIndex].quantity === 1) {
            // could disabled the button if i felt so inclined
        } else {
            currentCart[productIndex].quantity -= 1;
        }
        const updatedCart = currentCart;
        sessionStorage.setItem("userShoppingCart", JSON.stringify(updatedCart));
        updateCart(updatedCart);
    }

    return (
        <Box sx={{ display: "grid", gridTemplateColumns: "auto", gridTemplateRows: "50px auto" }}>
            <Typography variant="h6" component="div" sx={{ ml: 1, mt: 1, padding: "5px" }}>{`You have ${cart.length} item(s) in your cart`}</Typography>
            <Box sx={{ padding: '10px', display: 'grid', gridTemplateColumns: "auto", gridTemplateRows: "auto" }}>
                <Box sx={{ display: "grid", gridTemplateRows: "auto", gridTemplateColumns: "50% 50%" }}>
                    <Card elevation={3} sx={{ display: "grid", gridTemplateColumns: "auto", gridTemplateRows: "auto 50px", rowGap: "5px", ml: 1, mr: 1 }}>
                        <List component="nav">
                            {
                                cart.map((product) => (
                                    <ListItem key={product.productId} selected={selectedProductId === product.productId} sx={{ display: "grid", gridTemplateRows: "auto", gridTemplateColumns: "1fr max-content max-content max-content max-content"}}>
                                        <Button variant="text" sx={{ textAlign: "left" }} onClick={() => { setSelectedProductId(product.productId); }}>{product.productName}</Button>
                                        <IconButton onClick={() => { increaseProductQuantity(product) }}><AddCircle/></IconButton>
                                        <Typography>{ cart.find((productInCart) => { return productInCart.productId === product.productId }).quantity }</Typography>
                                        <IconButton onClick={() => { decreaseProductQuantity(product) }}><RemoveCircle/></IconButton>
                                        <IconButton onClick={() => { removeProductFromCart(product) }}><DeleteIcon/></IconButton>
                                    </ListItem>
                                ))
                            }
                        </List>
                        <Box sx={{ display: "grid" }}>
                            <Button variant="contained" color="primary" onClick={() => { handleCreateOrder() }} disabled={isCheckoutButtonDisabled}>
                                { isOrderInFlight ? <CircularProgress size={25}/> : "Checkout" } 
                            </Button>
                        </Box>
                    </Card>
                    <Box key={`${selectedProductId}-media-box`}>
                        <Card key={`${selectedProductId}-media-card`} elevation={3}>
                            {console.log(selectedProductId+" from card")}
                            <CardMedia key={`${selectedProductId}-media`} component="img" sx={{ objectFit: "contain" }} image={`../../images/${selectedProductId}.jpeg?${Date.now()}`}/>
                        </Card>
                    </Box>
                </Box>
            </Box>
        </Box>
    )
}