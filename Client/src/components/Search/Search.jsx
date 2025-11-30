import "./search.css";
import { IoSearch } from "react-icons/io5";

export default function Search({pageName}) {
  return (
    <form action="" className='search-form'>
        <p>Welcome to your <span>VaultOps {pageName}</span></p>

        <button className="open-search">
            <IoSearch />
        </button>
    </form>
  )
}
