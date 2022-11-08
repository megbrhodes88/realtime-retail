import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Box } from '@mui/system';
import { Slider, Typography, Input, FormGroup, FormControlLabel, Checkbox, Button, unstable_useId } from '@mui/material';
import { v4 as uuid } from 'uuid';

export default function Simulate() {
    const [products, setProducts] = useState([]);
    const [didLoadProducts, setDidLoadProducts] = useState(false);
    const [secondsValue, setSecondsValue] = useState(60);

    // const products = [
    //     { productId: "2r96u3vx4i6k", carYear: "1990", carMake: "Lamborghini", carModel:"Countach", productSubtitle: "Networked well-modulated installation", productDesc: "n/a" },
    //     { productId: "5k70t1yx5v9s", carYear: "1999", carMake: "Lamborghini", carModel:"Diablo", productSubtitle: "Multi-channelled hybrid support", productDesc: "n/a" }
    // ]

    useEffect(() => {
        const promise = axios.get("http://localhost:8080/api/products", { "Content-Type": "application/json" });
        promise.then((res) => { 
            let updatedProducts = []
            res.data.forEach((product) => {
                product.discluded = false;
                updatedProducts.push(product);
            });
            setProducts(updatedProducts); 
            setDidLoadProducts(true); });
        promise.catch((err) => { console.warn(err); });
    }, [didLoadProducts])

    

    const handleSecondsSliderChange = (event, newValue) => {
        setSecondsValue(event.target.value);
    }
    const handleSecondsInputChange = (event) => {
        setSecondsValue(event.target.value === "" ? "" : Number(event.target.value));
        console.log(products);
    }
    const handleSecondsBlur = () => {
        if (secondsValue < 0) { setSecondsValue(0) }
    }
    const handleDiscludeCheckboxChange = (event) => {
        const targetId = event.target.id;
        let updatedProducts = [];
        products.forEach((product) => {
            product.discluded = (targetId === product.productId ? !product.discluded : product.discluded);
            updatedProducts.push(product);
        })
        setProducts(updatedProducts);
    }
    const randomizeDisabledProducts = () => {
        let updatedProducts = [];
        products.forEach((product) => {
            product.discluded = Math.random()<=0.5 ? true : false
            updatedProducts.push(product);
        });
        setProducts(updatedProducts);
    }

    const handleStartSimulation = () => {
        const interval = setInterval(() => {
            let orderItems = [];
            const randomNumProducts = Math.round(Math.random()*2+1);
            for (let i=0; i < randomNumProducts; i++) {
                const randProductIndex = Math.floor(Math.random()*products.length);
                const randProductQuantity = Math.round(Math.random()*4+1);
                orderItems.push({ 
                    productId: products[randProductIndex].productId,
                    quantityPurchased: randProductQuantity
                });
            }
            const order = { orderId: uuid(), orderItems: orderItems };
            const promise = axios.post(`http://localhost:8080/api/orders`, order, { "Content-Type" : "Application/json"})
            promise.then(() => console.log("New order: "+JSON.stringify(order)));
            promise.catch((error) => console.warn("Error: "+error));
        }, 1000);
        return (() => { clearInterval(interval) });
    }

    return (
        <React.Fragment>
            <Box sx={{ display: "grid", gridTemplateRows: "100px auto", gridTemplateColumns: "auto", padding: "15px" }}>
                <Box sx={{ ml: 1 }}>
                    <Typography variant="h4" component="div">Simulate</Typography>
                    <Typography variant="h8" component="div">Use the following form to control simulating purchases with some auto-pilot.</Typography>
                </Box>
                <Box sx={{ display: "grid", gridTemplateRows: "auto", gridTemplateColumns: "50% 50%", ml: 1 }}>
                    <Box>
                        <Box sx={{ padding: "15px" }}>
                            <Typography variant="h6" component="div">Select the amount of time to simulate activity.</Typography>
                            <Slider value={typeof secondsValue === 'number' ? secondsValue : 60} onChange={handleSecondsSliderChange}/>
                            <Input value={secondsValue} size="small" onChange={handleSecondsInputChange} onBlur={handleSecondsBlur} inputProps={{ step: 5, min: 0, type: "number" }}/>
                        </Box>
                    </Box>
                    <Box>
                        <Box sx={{ display: "grid", gridTemplateColumns: "auto", gridTemplateRows: "75px, auto", padding: "15px" }}>
                            <Typography variant="h6" component="div">Select products to be discluded.</Typography>
                            <FormGroup sx={{ display: "flex", flexDirection: "row" }}>
                                {
                                    products.map((product) => (
                                            <FormControlLabel key={"key"+product.productId} control={
                                                <Checkbox id={product.productId} checked={product.discluded} onChange={handleDiscludeCheckboxChange}/> 
                                            } label={product.carMake+" "+product.carModel}/>
                                    ))
                                }
                            </FormGroup>
                            <Button onClick={randomizeDisabledProducts}>Randomize</Button>
                        </Box>
                    </Box>
                </Box>
                <Box>
                    <Button onClick={handleStartSimulation}>Start</Button>
                </Box>
            </Box>
        </React.Fragment>
    )
}