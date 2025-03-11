/**
 * D3.js-based renderer for Scala.js Binary Search Tree
 * Expects a tree structure defined as:
 * enum BST {
 *   def color: Color = Color.Black
 *   case Empty
 *   case Node(value: Int, override val color: Color, left: BST, right: BST)
 * }
 */

import * as d3 from 'd3';

export class TreeRenderer {
  constructor(containerId, options = {}) {
    // Default configuration
    this.config = {
      width: 1000,
      height: 500,
      margin: { top: 40, right: 90, bottom: 50, left: 90 },
      nodeRadius: 20,
      ...options
    };

    // Calculate actual dimensions
    this.width = this.config.width - this.config.margin.left - this.config.margin.right;
    this.height = this.config.height - this.config.margin.top - this.config.margin.bottom;

    // Initialize SVG container
    this.svg = d3.select(`#${containerId}`)
      .append('svg')
      .attr('width', this.config.width)
      .attr('height', this.config.height)
      .append('g')
      .attr('transform', `translate(${this.config.margin.left},${this.config.margin.top})`);
  }

  /**
   * Converts Scala.js BST format to D3.js hierarchical format
   * @private
   */
  _convertToD3Format(node) {
    if (node.$type === "Empty") {
      return null;
    }

    return {
      value: node.value,
      color: node.color === "Red" ? "red" : "black",
      children: [
        this._convertToD3Format(node.left),
        this._convertToD3Format(node.right)
      ].filter(Boolean) // Remove null children
    };
  }

  /**
   * Renders a Binary Search Tree defined in Scala.js
   * @param {Object} bst - The Binary Search Tree in Scala.js format
   *                     - Can be either Empty or Node(value, color, left, right)
   */
  render(bst) {
    console.log(JSON.stringify(bst));
    // Clear the SVG
    this.svg.selectAll('*').remove();

    // If the tree isempty, return early
    if (bst.$type === "Empty") {
      return;
    }

    // Convert the BST to D3 hierarchical format
    const d3Data = this._convertToD3Format(bst);
    const root = d3.hierarchy(d3Data);

    // Create the tree layout
    const treeLayout = d3.tree().size([this.width, this.height]);
    const treeData = treeLayout(root);

    // Add links between nodes
    this.svg.selectAll('.link')
      .data(treeData.links())
      .enter()
      .append('path')
      .attr('class', 'link bg-gray-500')
      .attr('d', d3.linkVertical()
        .x(d => d.x)
        .y(d => d.y));

    // Add nodes
    const nodes = this.svg.selectAll('.node')
      .data(treeData.descendants())
      .enter()
      .append('g')
      .attr('class', 'node')
      .attr('transform', d => `translate(${d.x},${d.y})`);

    // Add circles for nodes
    nodes.append('circle')
      .attr('r', this.config.nodeRadius)
      .style('fill', d => d.data.color)
      .style('stroke', '#000');

    // Add text to nodes
    nodes.append('text')
      .attr('dy', '0.35em')
      .attr('text-anchor', 'middle')
      .style('fill', d => d.data.color === "black" ? 'white' : 'black')
      .text(d => d.data.value);
  }

  /**
   * Updates the dimensions of the tree visualization
   * @param {number} width - New width of the visualization
   * @param {number} height - New height of the visualization
   */
  resize(width, height) {
    this.config.width = width;
    this.config.height = height;
    this.width = width - this.config.margin.left - this.config.margin.right;
    this.height = height - this.config.margin.top - this.config.margin.bottom;

    this.svg.select('svg')
      .attr('width', this.config.width)
      .attr('height', this.config.height);
  }

}
