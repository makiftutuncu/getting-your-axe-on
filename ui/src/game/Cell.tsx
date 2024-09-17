import { TableCell } from "@mui/material"
import { blueGrey } from "@mui/material/colors"

type Props = {
    x: number
    y: number
    content: string
    enabled?: boolean
    selected?: boolean
    onClick?: () => void
}

const symbols = {
    "A": "🚢",
    "B": "⛴️",
    "X": "💥",
    "O": "❌",
}

const Cell = ({ x, y, content, enabled, selected, onClick }: Props) => {
    return (
        <TableCell
            sx={{
                cursor: (enabled && x >= 0 && y >= 0) ? "pointer" : "",
                background: selected ? blueGrey[100] : "",
                textAlign: "center",
            }}
            onClick={() => onClick && onClick()}>
            {Object.entries(symbols).find(([key]) => key === content)?.pop() || content}
        </TableCell>
    )
}

export default Cell
