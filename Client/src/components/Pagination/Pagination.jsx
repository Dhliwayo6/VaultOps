import "./pagination.css";

export default function Pagination({itemsPerPage, totalItems, paginate, currentPage}) {
    const pageNumbers = [];

    for (let i = 1; i< Math.ceil(totalItems / itemsPerPage); i++){
        pageNumbers.push(i);
    }


  return (
    <nav className="pagination-nav">
        <ul>
            {
                pageNumbers.map(number => {
                    return <li key={number} >
                        <button onClick={()=> paginate(number)} className={`${currentPage === number? "active-page" : "page-item"}`}>
                            {number}
                        </button>
                    </li>
                })
            }
        </ul>
    </nav>
  )
}