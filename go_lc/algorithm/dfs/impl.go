package dfs

import (
	"fmt"
	"github.com/bytedance/sonic"
	funk "github.com/thoas/go-funk"
	"go_lc/common/graph/directed"
)

func RunDemo() {
	g := directed.NewGraph()
	g.UpsertNode("A", 0).
		UpsertNode("B", 0).
		UpsertNode("C", 0).
		UpsertNode("D", 0).
		UpsertNode("E", 0).
		UpsertNode("F", 0).
		UpsertNode("G", 0).
		UpsertEdge("A", "B", 0).
		UpsertEdge("A", "C", 0).
		UpsertEdge("C", "D", 0).
		UpsertEdge("C", "E", 0).
		UpsertEdge("C", "F", 0).
		UpsertEdge("F", "A", 0).
		UpsertEdge("F", "D", 0)
	visitList := []string{}
	for _, node := range g.Nodes() {
		dfs(g, node.Name, visitList)
	}
}

// dfs 实际遍历函数
// g: 图
//
func dfs(g *directed.Graph, nodeName string, visitList []string) {
	// 0. 输出节点名称
	jsonStr, _ := sonic.MarshalString(visitList)
	println(
		fmt.Sprintf("当前节点: %s , 当前访问列表: %s", nodeName, jsonStr),
	)

	// 1. Node 如果没有下游节点, 则返回
	if len(g.GetOutputEdges(nodeName)) == 0 {
		return
	}

	// 2. 已经遍历, 说明存在环, 终止
	if funk.ContainsString(visitList, nodeName) {
		return
	}

	// 3. 将该节点标记为已经访问
	visitList = append(visitList, nodeName)

	// 4. 深度遍历
	for _, edge := range g.GetOutputEdges(nodeName) {
		dfs(g, edge.To.Name, visitList)
	}

}
