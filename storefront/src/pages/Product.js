import { useParams } from "react-router-dom";
import { Box, Grid, Typography, Card, CardMedia, CardContent, CardActions, Button } from "@mui/material";
import { keyframes } from '@mui/system';
import { useContext, useEffect, useState, useLayoutEffect } from "react";
import { CartContext } from "../contexts/CartContext";
import { useSubscription } from 'react-stomp-hooks';
import axios from 'axios';

export default function Product() {
    const APISERVER_HOSTNAME = "a2d186108d0264bd7a8bec10e384d8c3-635988121.us-east-1.elb.amazonaws.com";
    const { addOrRemoveItemsInCart } = useContext(CartContext);
    const [cart, updateCart] = useState([]);
    const { productId } = useParams();
    const [product, setProduct] = useState({});
    const [refresh, setRefresh] = useState(true);

    const flicker = keyframes`
        50% {
            opacity: 0;
        }
    `;  

    useSubscription(`/topic/products/${product.productId}`, (res) => {
        setProduct(JSON.parse(res.body));
    });

    window.onpageshow = () => {
        setRefresh(true);
    }

    useEffect(() => {
            const promise = axios.get(`http://${APISERVER_HOSTNAME}:8080/api/products/${productId}`, { "Content-Type": "application/json" });
            promise.then((res) => { setProduct(res.data); setRefresh(false); });
            promise.catch((err) => { console.warn(err); });
    }, [refresh]);

    useLayoutEffect(() => {
        if (sessionStorage.getItem("userShoppingCart") === null) {
            sessionStorage.setItem("userShoppingCart", JSON.stringify([]));
        } else if (sessionStorage.getItem("userShoppingCart") === "[]") {
        } else {
            const currentCart = JSON.parse(sessionStorage.getItem("userShoppingCart"));
            updateCart(currentCart);
        }
    }, []);

    const addProductToCart = (product) => {
        const currentCart = JSON.parse(sessionStorage.getItem("userShoppingCart"));
        // check if item already in cart
        if (currentCart.find((productInCart) => { return productInCart.productId === product.productId })) {
            console.log('in cart already');
        } else {
            let updatedCart = currentCart;
            product.quantity = 1;
            updatedCart.push(product);
            sessionStorage.setItem("userShoppingCart", JSON.stringify(updatedCart));
            addOrRemoveItemsInCart(updatedCart.length)
        }
    }
    
    const getProductInv = (product) => {
        if (product) {
            return product.inventory;
        }
    }

    const prettyNumbers = (number) => {
        if (number) {
            return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        }
        
    }

    return (
        <div className="product">
            <Box sx={{ padding: '25px' }}>
                <Box sx={{ padding: '10px', display: 'flex', gap: '10px' }}>
                    <Grid container spacing={2}>
                        <Grid item xs={7}>
                            <Card elevation={3}>
                                <CardMedia component="img" image={`../../images/${productId}.jpeg`}/>
                            </Card>
                        </Grid>
                        <Grid item xs={5}>
                            <Card elevation={3}>
                                <CardContent>
                                    <Typography gutterBottom variant="h4" component="div">{`${product.carYear} ${product.carMake} ${product.carModel}`}</Typography>
                                    <Typography variant="body2" color="text.secondary">{`${product.productSubtitle}.`}</Typography>
                                    <Typography variant="body2"><br/>{`${product.productDesc}`}</Typography>
                                    <br/>
                                    <Box sx={{ display: "grid", gridTemplateRows: "max-content", gridTemplateColumns: "max-content max-content"}}>
                                        <Typography variant="body2" color="text.secondary" sx={{ mr: "5px" }}>Available Inventory: </Typography>
                                        <Typography key={`${product.productId}-${product.inventory}-inventory`} variant="body2" color="text.secondary" sx={{ animation: `${flicker} 1s linear` }}>{getProductInv(product)}</Typography>
                                    </Box>
                                    {
                                        product.discount === 0
                                        ? (<Box sx={{ display: "grid", gridTemplateRows: "max-content", gridTemplateColumns: "max-content max-content", mb: "5px"}}>
                                            <Typography variant="body2" color="text.secondary" align="left" sx={{ mr: "5px" }}>Current Price: </Typography>
                                            <Typography key={`${product.productId}-${product.productPrice}-list`} variant="body2" color="text.secondary" align="left" sx={{ mr: "5px", animation: `${flicker} 1s linear` }}>${prettyNumbers(product.productPrice)}</Typography>
                                        </Box>)
                                        : (<Box sx={{ display: "grid", gridTemplateRows: "max-content", gridTemplateColumns: "max-content max-content max-content", mb: "5px"}}>
                                            <Typography variant="body2" color="text.secondary" align="left" sx={{ mr: "5px" }}>Current Price: </Typography>
                                            <Typography key={`${product.productId}-${product.productPrice}-list`} variant="body2" color="red" align="left" sx={{ mr: "5px", animation: `${flicker} 1s linear` }}><s>${prettyNumbers(product.productPrice)}</s></Typography>
                                            <Typography key={`${product.productId}-${product.productPrice}-discounted`}variant="body2" color="text.secondary" align="left" sx={{ animation: `${flicker} 1s linear` }}>${prettyNumbers(product.productPrice*(1-product.discount/100))}</Typography>
                                        </Box>)
                                    }
                                </CardContent>
                                <CardActions>
                                    <Button color="primary" onClick={() => { addProductToCart(product) }}>Add to Cart</Button>
                                </CardActions>
                            </Card>
                        </Grid>
                    </Grid>
                </Box>
            </Box>
        </div>
    )
}