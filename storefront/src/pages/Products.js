import { Box, Typography } from '@mui/material';
import { useState, useEffect } from 'react';
import { useSubscription } from 'react-stomp-hooks';
import axios from 'axios';
import ProductCard from '../components/ProductCard';

export default function Products() {
    const APISERVER_HOSTNAME = "a2d186108d0264bd7a8bec10e384d8c3-635988121.us-east-1.elb.amazonaws.com";    
    const [products, setProducts] = useState([]);
    const [refresh, setRefresh] = useState(true);

    useSubscription('/topic/products', (message) => {
        const productUpdate = JSON.parse(message.body);
        const productIndex = products.findIndex((product) => { return product.productId === productUpdate.productId });
        products.splice(productIndex, 1, productUpdate);
        setProducts([...products]);
    });

    window.onpageshow = () => {
        setRefresh(true);
    }

    useEffect(() => {
            const promise = axios.get(`http://${APISERVER_HOSTNAME}:8080/api/products`, { "Content-Type": "application/json" });
            promise.then((response) => { setProducts(response.data); setRefresh(false); });
            promise.catch((error) => { console.warn(error); });
    }, [refresh]);

    return (
        <div className="products">
            <Box sx={{ padding: '10px' }}>
                <Typography variant="h4" component="div" sx={{ padding: '5px' }}>Products</Typography>
                <Typography variant="h6" component="div" sx={{ padding: '5px' }}>{products.length} results:</Typography>
                <Box sx={{ padding: '10px', display: 'flex', flexWrap: 'wrap', justifyContent: "center", gap: '10px' }}>
                    {
                        products.map((product) => (
                            <ProductCard key={product.productId} product={product}/>
                        ))
                    }
                </Box>
            </Box>
        </div>
    );
}