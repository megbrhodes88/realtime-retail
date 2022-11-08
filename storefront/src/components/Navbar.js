import { useState, useContext } from 'react';
import { AppBar, Badge, Box, Drawer, IconButton, List, ListItem, ListItemText, Toolbar } from '@mui/material';
import { Button } from '@mui/material';
import { Outlet, Link } from "react-router-dom";
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import { CartContext } from '../contexts/CartContext';
import { Menu } from '@mui/icons-material';

// THIS PROBABLY SHOULDNT BE THE ROOT ELEMENT FOR THE ROUTER
// PROBABLY BETTER TO PAGE THIS A "PAGE" CLASS THAT
// IMPORTS THE NAVBAR AND THEN ADDS THE PAGE OUTLET BELLOW
// IDK 
export default function Navbar() {
    const { itemsInCart } = useContext(CartContext);
    const [ isMenuOpen, setIsMenuOpen ] = useState(false);

    const toggleMenuDrawer = () => (event) => {
        if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
          return;
        }
        setIsMenuOpen(!isMenuOpen);
      };

    return (
        // AND THIS WOULD BE GOOD TO RENAME SINCE THIS RENDERS MORE THAN JUST THE NAVBAR, DONT FORGET TO UPDATE CSS
        <Box sx={{ display: "grid", gridTemplateRows: "75px auto", gridTemplateColumns: "100vw", gridTemplateAreas: "'navigation' 'page'" }} >
            <Box xs={{ display: "grid", gridTemplateRows: "auto", gridTemplateColumns: "100vw", gridArea: "navigation" }} >
                <AppBar position="static" sx={{ bgcolor: "black" }}>
                    <Toolbar>
                        <DirectionsCarIcon fontSize='large' sx={{ mr: 2, display: { xs: 'none', md: 'flex' } }}/>
                        <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
                            <Button href="/" page="Products" sx={{ my: 2, color: 'white', display: 'block', textAlign: 'center' }}>Home</Button>
                            <Button href="/sales" disabled page="Sales" sx={{ my: 2, color: 'white', display: 'block', textAlign: 'center' }}>Sales</Button>
                        </Box>
                        <IconButton size="large" component={Link} to={`/cart`} color="inherit">
                            <Badge badgeContent={itemsInCart} color="error">
                                <ShoppingCartIcon/>
                            </Badge>
                        </IconButton>
                        <IconButton color="inherit" onClick={() => { setIsMenuOpen(true); }}>
                            <Menu></Menu>
                        </IconButton>
                    </Toolbar>
                    <Drawer anchor="right" open={isMenuOpen} onClose={toggleMenuDrawer(false)}>
                        <Box sx={{ display: "grid", gridTemplateColumns: "250px", gridTemplateRows: "auto" }}>
                            <List>
                                <ListItem button component="a" href="/simulate">
                                    <ListItemText>Simulate</ListItemText>
                                </ListItem>
                                <ListItem button component="a" href="/liveview" disabled={true}>
                                    <ListItemText>LiveView</ListItemText>
                                </ListItem>
                            </List>
                        </Box>
                    </Drawer>
                </AppBar>
            </Box>
            <Box sx={{ gridArea: "page", display: "grid", gridTemplateColumns: "auto", gridTemplateRows: "auto" }} >
                <Outlet/>
            </Box>
        </Box>
    );
}