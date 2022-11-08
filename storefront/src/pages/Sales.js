import { useEffect, useState } from 'react';
import axios from 'axios';

export default function Sales() {
    const [products, setProducts] = useState([]);
    const [didLoadProducts, setDidLoadProducts] = useState(false);

    return (
        <div className="sales">
            Sales - comin' soon.
        </div>
    )
}