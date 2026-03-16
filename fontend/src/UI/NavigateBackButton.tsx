import { FaLock, FaUnlock, FaArrowUp, FaArrowDown, FaArrowLeft } from "react-icons/fa6";
import { useNavigate } from "react-router-dom";

interface NavigateBackButtonProps {
    title: string;
}


export default function NavigateBackButton({title}: NavigateBackButtonProps) {
    const navigate = useNavigate();
  return (
        <button 
        onClick={() => navigate(-1)}
        className="flex items-center gap-2 text-slate-500 hover:text-red-600 transition-colors font-semibold"
        >
            <FaArrowLeft />
            {title}
        </button>
    )
}
