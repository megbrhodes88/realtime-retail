import React from 'react';
import { Link } from 'react-router-dom';
import { Box, Card, CardActionArea, CardContent, CardMedia, Typography } from '@mui/material';
import { keyframes } from '@mui/system';
// import { v4 as uuid } from 'uuid';

const ProductCard = ({product}) => {
    const prettyNumbers = (number) => {
        return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }

    const flicker = keyframes`
        50% {
            opacity: 0;
        }
    `;

    return (
        <Card sx={{ display: "grid", gridTemplateColumns: "100%", gridTemplateRows: "90% 10%", width: 250 }}>
            {/* <CardActionArea href={`/products/${product.productId}`}> */}
            <CardActionArea component={Link} to={`/products/${product.productId}`}>
                <CardMedia component="img" height="150" width="175" image={`images/${product.productId}.jpeg`}/>
                <CardContent>
                    {console.log("rerender")}
                    <Typography gutterBottom variant="h7" component="div">{`${product.carMake} ${product.carModel}`}</Typography>
                    <Typography variant="body2" color="text.secondary">
                        {`${product.productSubtitle}.`}
                    </Typography>
                </CardContent>
            </CardActionArea>
            {
                product.discount === 0
                ? (<Box sx={{ display: "grid", gridTemplateRows: "100%", gridTemplateColumns: "max-content 1fr", mb: "5px"}}>
                    <Typography key={`${product.productId}-${product.inventory}`} variant="body2" color="text.secondary" align="left" sx={{ ml: "16px", animation: `${flicker} 1s linear` }}>{product.inventory}</Typography>
                    <Typography key={`${product.productId}-${product.discount}-list`} variant="body2" color="text.secondary" align="center" sx={{ animation: `${flicker} 1s linear` }}>${prettyNumbers(product.productPrice)}</Typography>
                </Box>)
                : (<Box sx={{ display: "grid", gridTemplateRows: "max-content", gridTemplateColumns: "max-content 1fr 1fr", mb: "5px"}}>
                    <Typography key={`${product.productId}-${product.inventory}`} variant="body2" color="text.secondary" align="left" sx={{ ml: "16px", animation: `${flicker} 1s linear` }}>{product.inventory}</Typography>
                    <Typography key={`${product.productId}-${product.discount}-list`} variant="body2" color="red" align="right" sx={{ animation: `${flicker} 1s linear` }}><s>${prettyNumbers(product.productPrice)}</s></Typography>
                    <Typography key={`${product.productId}-${product.discount}-discounted`} variant="body2" color="text.secondary" align="center" sx={{ animation: `${flicker} 1s linear` }}>${prettyNumbers(product.productPrice*(1-product.discount/100))}</Typography>
                </Box>)
            }
        </Card>
    );
};

export default React.memo(ProductCard);