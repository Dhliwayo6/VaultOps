import { IoIosCheckmarkCircleOutline } from "react-icons/io";
import { FaArrowTrendUp } from "react-icons/fa6";
import { BsClock } from "react-icons/bs";
import { PiWarningCircle } from "react-icons/pi";

export const conditions = ()=> {
    return [
        {
            icon: <IoIosCheckmarkCircleOutline style={{"color": "#21A554", "fontSize": "1.5rem"}} />,
            title: "Excellent",
            itemCount: 25,
            color: "#21A554"
        },
        {
            icon: <FaArrowTrendUp style={{"color": "#33CC80", "fontSize": "1.5rem"}} />,
            title: "Good",
            itemCount: 25,
            color: "#33CC80"
        },
        {
            icon: <BsClock style={{"color": "#F59E0B", "fontSize": "1.5rem"}} />,
            title: "Fair",
            itemCount: 25,
            color: "#F59E0B"
        },
        {
            icon: <PiWarningCircle style={{"color": "#FA6C1E", "fontSize": "1.5rem"}} />,
            title: "Bad",
            itemCount: 25,
            color: "#FA6C1E"
        },
        {
            icon: <PiWarningCircle style={{"color": "#EB4D4D", "fontSize": "1.5rem"}} />,
            title: "Damaged",
            itemCount: 25,
            color: "#EB4D4D"
        },
    ];
}