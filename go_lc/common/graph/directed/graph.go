package directed

// Node 节点
type Node struct {
	Name string
	Val  int64
}

func NewNode(name string, val int64) *Node {
	return &Node{
		Name: name,
		Val:  val,
	}
}

// Edge 边
type Edge struct {
	Val  int64 // 边权重
	From *Node
	To   *Node
}

func NewEdge(from *Node, to *Node, val int64) *Edge {
	return &Edge{
		From: from,
		To:   to,
		Val:  val,
	}
}

// Graph 单向图
type Graph struct {
	nodes []*Node
	edges []*Edge
}

func (g *Graph) Nodes() []*Node {
	return g.nodes
}

func (g *Graph) Edges() []*Edge {
	return g.edges
}

func NewGraph() *Graph {
	return &Graph{
		nodes: []*Node{},
		edges: []*Edge{},
	}
}

func (g *Graph) UpsertNode(name string, val int64) *Graph {
	for _, node := range g.nodes {
		if node.Name == name {
			node.Val = val // 更新
			return g
		}
	}
	g.nodes = append(g.nodes, NewNode(name, val))
	return g
}

func (g *Graph) UpsertEdge(from, to string, val int64) *Graph {
	for _, edge := range g.edges {
		// 更新
		if edge.From.Name == from && edge.To.Name == to {
			edge.Val = val
			return g
		}
	}

	g.edges = append(g.edges, NewEdge(g.GetNode(from), g.GetNode(to), val))
	return g
}

func (g *Graph) GetNode(name string) *Node {
	for _, node := range g.nodes {
		if node.Name == name {
			return node
		}
	}
	panic("NodeNotExist")
}

// GetOutputEdges 获取节点入边
func (g *Graph) GetOutputEdges(nodeName string) []*Edge {
	ret := make([]*Edge, 0, len(g.edges))
	for _, edge := range g.edges {
		if edge.From.Name == nodeName {
			ret = append(ret, edge)
		}
	}
	return ret
}

// GetInputEdges 获取节点入边
func (g *Graph) GetInputEdges(nodeName string) []*Edge {
	ret := make([]*Edge, 0, len(g.edges))
	for _, edge := range g.edges {
		if edge.To.Name == nodeName {
			ret = append(ret, edge)
		}
	}
	return ret
}
