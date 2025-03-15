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
    this.config = {
      width: 1000,
      height: 500,
      nodeRadius: 20,
      margin: { top: 40, right: 90, bottom: 50, left: 90 },
      ...options
    };

    // Set up the SVG container with margins
    const width = this.config.width - this.config.margin.left - this.config.margin.right;
    const height = this.config.height - this.config.margin.top - this.config.margin.bottom;

    this.svg = d3.select(`#${containerId}`)
      .append('svg')
      .attr('width', this.config.width)
      .attr('height', this.config.height)
      .append('g')
      .attr('transform', `translate(${this.config.margin.left},${this.config.margin.top})`);

    // Store dimensions for tree layout
    this.dimensions = { width, height };
  }

  /**
   * Converts BST format to D3.js hierarchical format
   * Adds invisible nodes to maintain consistent tree structure
   */
  _convertToD3Format(node) {
    if (node.$type === "Empty") return null;

    const leftChild = this._convertToD3Format(node.left);
    const rightChild = this._convertToD3Format(node.right);
    const children = [];

    // Handle left side
    if (leftChild || rightChild) {
      children.push(leftChild || { value: '', color: 'transparent', isInvisible: true });
    }

    // Handle right side
    if (rightChild || leftChild) {
      children.push(rightChild || { value: '', color: 'transparent', isInvisible: true });
    }

    return {
      value: node.value,
      color: node.color === "Red" ? "red" : "black",
      label: node.label || '',
      children: children
    };
  }

  /**
   * Renders the Binary Search Tree
   */
  render(bst) {
    if (bst.$type === "Empty") return;

    // Set up the tree data
    const root = d3.hierarchy(this._convertToD3Format(bst));
    const treeLayout = d3.tree()
      .size([this.dimensions.width, this.dimensions.height]);
    const treeData = treeLayout(root);

    // Clear previous content
    this.svg.selectAll('*').remove();

    // Draw links
    this.svg.selectAll('.link')
      .data(treeData.links())
      .enter()
      .append('path')
      .attr('class', 'link')
      .style('opacity', d => d.target.data.isInvisible ? 0 : 1)
      .style('stroke', d => {
        // Only make link blue if both source and target nodes have labels
        const sourceHasLabel = d.source.data.label && d.source.data.label !== '';
        const targetHasLabel = d.target.data.label && d.target.data.label !== '';
        return (sourceHasLabel && targetHasLabel) ? '#3b82f6' : '#374151';
      })
      .style('stroke-width', '2px')
      .attr('d', d => `M${d.source.x},${d.source.y}L${d.target.x},${d.target.y}`);

    // Create node groups
    const nodes = this.svg.selectAll('.node')
      .data(treeData.descendants())
      .enter()
      .append('g')
      .attr('class', 'node')
      .style('opacity', d => d.data.isInvisible ? 0 : 1)
      .attr('transform', d => `translate(${d.x},${d.y})`);

    // Add circles with drop shadow
    const defs = this.svg.append('defs');

    // Define drop shadow filter
    const filter = defs.append('filter')
      .attr('id', 'drop-shadow')
      .attr('height', '130%');

    filter.append('feGaussianBlur')
      .attr('in', 'SourceAlpha')
      .attr('stdDeviation', 2)
      .attr('result', 'blur');

    filter.append('feOffset')
      .attr('in', 'blur')
      .attr('dx', 0)
      .attr('dy', 2)
      .attr('result', 'offsetBlur');

    const feMerge = filter.append('feMerge');
    feMerge.append('feMergeNode')
      .attr('in', 'offsetBlur');
    feMerge.append('feMergeNode')
      .attr('in', 'SourceGraphic');

    // Add circles with shadow
    nodes.append('circle')
      .attr('r', this.config.nodeRadius)
      .style('fill', d => d.data.color)
      .style('stroke', d => d.data.label ? '#3b82f6' : 'none') // Blue border for labeled nodes
      .style('stroke-width', d => d.data.label ? '3px' : '0')
      .style('filter', 'url(#drop-shadow)');

    // Add value labels
    nodes.append('text')
      .attr('dy', '0.35em')
      .attr('text-anchor', 'middle')
      .text(d => d.data.value);

    // Add node labels (if present)
    nodes.filter(d => d.data.label)
      .append('text')
      .attr('dy', '-1.5em')
      .attr('text-anchor', 'middle')
      .attr('class', 'label')
      .style('font-weight', 'bold')
      .style('fill', '#3b82f6')
      .text(d => d.data.label);
  }

  /**
   * Updates the dimensions of the tree visualization
   * @param {number} width - New width of the visualization
   * @param {number} height - New height of the visualization
   */
  resize(width, height) {
    this.config.width = width;
    this.config.height = height;
    this.dimensions.width = width - this.config.margin.left - this.config.margin.right;
    this.dimensions.height = height - this.config.margin.top - this.config.margin.bottom;

    this.svg.select('svg')
      .attr('width', this.config.width)
      .attr('height', this.config.height);
  }
}
